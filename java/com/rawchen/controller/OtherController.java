package com.rawchen.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageHelper;
import com.rawchen.domain.Content;
import com.rawchen.domain.Tag;
import com.rawchen.utils.LogUtil;
import com.rawchen.utils.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

import static com.rawchen.utils.DateTimeUtil.*;
import static com.rawchen.utils.StringUtil.stringToList;

@Controller
public class OtherController extends BaseController {

	@RequestMapping({"/", "/index"})
	public String index(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
						Model model){

		//插入日志
		logService.insert(LogUtil.insertLog(request,"/"));

		int numberOfArticles = contentService.selectNumberOfArticles();
		model.addAttribute("numberOfArticles",numberOfArticles);

//		从 Servlet 上下文中获取应用程序选项映射，并从中提取名为 "postsListSize" 的属性值。
//		这个属性值表示每页显示的数据量（即每页显示的文章数量）。
//		使用 PageHelper.startPage() 方法设置分页的起始页和每页显示的数据量，
//		其中 pageNum 表示当前页码，postsListSize 表示每页显示的数据量。
		int postsListSize = Integer.parseInt((String) ((Map) request.getServletContext().getAttribute("applicationOptionsMap")).get("postsListSize"));
		PageHelper.startPage(pageNum, postsListSize);
		List<Content> contents = contentService.selectAllContent();
		for (Content c : contents) {

			//查询设置评论数
			c.setCommentCount(contentService.selectCommentCountByCid(c.getCid()));

			c.setCategoryName(categoryService.selectByPrimaryKey(c.getCgid()).getCgName());
			c.setCategorySlug(categoryService.selectByPrimaryKey(c.getCgid()).getCgSlug());

			System.out.println("评论数: " + c.getCommentCount());
			System.out.println("类别名称: " + c.getCategoryName());
			System.out.println("类别标识: " + c.getCategorySlug());


			List<Tag> tags = new ArrayList<>();
			List<String> strings = stringToList(c.getTagList());
			for (String s : strings) {
				Tag tag = tagService.findTagById(Integer.parseInt(s));
				if (tag != null) {
					tags.add(tag);
				}
			}
			c.setTList(tags);
		}

		model.addAttribute("contents",contents);
		long totalPages = PageHelper.count(()->contentService.selectAllContent());
		totalPages = (totalPages % postsListSize) == 0 ? totalPages/postsListSize : (totalPages/postsListSize) + 1;
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("pageHelper", 1);

		List<Content> recommendContents = contentService.selectRecommendContent();
		model.addAttribute("recommendContents",recommendContents);

		return "index";
	}

	@ResponseBody
	@RequestMapping(value = "/upFile", method = RequestMethod.POST)
	public Map<String,Object> articleUpFile(HttpServletRequest req, @RequestParam("editormd-image-file") MultipartFile picpaths) throws JsonProcessingException {
		Map<String, Object> map = new HashMap<>();

		String url = "blog/" + StringUtil.getDateToString(new Date()) + "/";
		String realPath = req.getServletContext().getRealPath("/upload/");

		File file = new File(realPath, url);
		if (!file.exists() && file.mkdirs()) {
		}
		String originalFileName = picpaths.getOriginalFilename();
		String newFileSName = "";

		// 限制文件类型
		if (originalFileName != null && (originalFileName.toLowerCase().endsWith(".jsp") || originalFileName.toLowerCase().endsWith(".asp"))) {
			map.put("success", 0);
			map.put("message", "无法上传类型为jsp和asp的文件");
			return map;
		}

		if (originalFileName.contains(".")) {
			newFileSName = originalFileName.substring(originalFileName.lastIndexOf("."));
		}
		String upPicFileName = System.currentTimeMillis() + newFileSName;
		file = new File(file, upPicFileName);
		String contextPath = req.getServletContext().getContextPath();
		url = contextPath + "/upload/" + url + upPicFileName;
		try {
			picpaths.transferTo(file);
			map.put("success", 1);
			map.put("message", "上传成功");
			map.put("url", url);// 拼接自己的地址
		} catch (Exception e) {
			map.put("success", 0);
			map.put("message", "上传失败");
		}
		return map;
	}

	@ResponseBody
	@RequestMapping("/getTodayYesterdayAccess")
	public Map<String,Object> getTodayYesterdayAccess() {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> dataMap = new HashMap<>();
		Map<String, Object> mapYesterday = new HashMap<>();
		Map<String, Object> mapToday = new HashMap<>();
		List<Integer> y = logService.selectYesterdayPvUvIndexGuestbook();
		mapYesterday.put("a",y.get(0));
		mapYesterday.put("b",y.get(1));
		mapYesterday.put("c",y.get(2));
		mapYesterday.put("d",y.get(3));
		mapYesterday.put("e",y.get(4));

		List<Integer> t = logService.selectTodayPvUvIndexGuestbook();
		mapToday.put("a",t.get(0));
		mapToday.put("b",t.get(1));
		mapToday.put("c",t.get(2));
		mapToday.put("d",t.get(3));
		mapToday.put("e",t.get(4));

		map.put("code", 0);
		map.put("info", "success");

		dataMap.put(yesterdayDateConvertString(),mapYesterday);
		dataMap.put(todayDateConvertString(),mapToday);

		map.put("data",dataMap);

		return map;
	}

	@ResponseBody
	@RequestMapping("/getLastWeekendAccess")
	public Map<String,Object> getLastWeekendAccess() {
		Map<String, Object> map = new HashMap<>();

		List<Integer> pv = logService.selectSevenDaysPv();
		List<Integer> uv = logService.selectSevenDaysUv();

		List<Map<String,Object>> l = new ArrayList<>();
		for (int i = 30; i > 0; i--) {
			Map<String, Object> mapLastWeek = new HashMap<>();
			mapLastWeek.put("date",calculateApartDayConvertString(i));
			mapLastWeek.put("pv",pv.get(i-1));
			mapLastWeek.put("uv",uv.get(i-1));
			l.add(mapLastWeek);
		}

		map.put("code", 0);
		map.put("info", "success");

		map.put("data",l);

		return map;
	}

//	@RequestMapping("/{page}")
//	public String test(@PathVariable String page) {
//		return page;
//	}
}
