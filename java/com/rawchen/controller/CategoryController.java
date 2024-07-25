package com.rawchen.controller;

import com.rawchen.domain.Category;
import com.rawchen.domain.Content;
import com.rawchen.domain.Tag;
import org.apache.poi.ss.usermodel.Row;
import com.rawchen.utils.FileUtil;


import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;

import static com.rawchen.utils.StringUtil.stringToList;

@Controller
public class CategoryController extends BaseController {

	@RequestMapping("/category/{cgid}")
	public String category(@PathVariable int cgid, Model model) {

		//查询所有category实体
		List<Category> categories = categoryService.selectAllCategory();
		for (Category c : categories) {
			c.setCount(contentService.selectContentCountByCgid(c.getCgid()));
		}
		model.addAttribute("categories", categories);

		//查询所有content实体
		List<Content> contents = contentService.selectContentListByCgid(cgid);
		for (Content c : contents) {

			//查询设置评论数
			c.setCommentCount(contentService.selectCommentCountByCid(c.getCid()));

			c.setCategoryName(categoryService.selectByPrimaryKey(c.getCgid()).getCgName());
			c.setCategorySlug(categoryService.selectByPrimaryKey(c.getCgid()).getCgSlug());

			List<Tag> tags = new ArrayList<>();
			List<String> strings = stringToList(c.getTagList());
			for (String s : strings) {
				tags.add(tagService.findTagById(Integer.parseInt(s)));
			}
			c.setTList(tags);
		}
		model.addAttribute("contents", contents);

		List<Content> recommendContents = contentService.selectRecommendContent();
		model.addAttribute("recommendContents", recommendContents);

		Map<String, String> optionsMap = new HashMap<>();
		optionsMap.put("categoryId", String.valueOf(cgid));
		model.addAttribute("optionsMap", optionsMap);
		return "category";
	}

	@RequestMapping("/updateCategory")
	@ResponseBody
	public Map<String,Object> updateCategory(Category category){
		Map<String, Object> map = new HashMap<>();
		int a = categoryService.updateCategory(category);
		if (a == 1) {
			map.put("data", "更新分类成功");
		} else {
			map.put("data", "更新分类失败");
		}
		return map;
	}

	@RequestMapping("/deleteCategory")
	@ResponseBody
	public Map<String,Object> deleteCategory(int cgid){
		Map<String, Object> map = new HashMap<>();
		List<Content> contentList = contentService.selectAllContent();
		for (Content content : contentList) {
			if (content.getCgid() == cgid) {
				//根据content的id修改cgid
				int a = contentService.updateContentCgidDefaultByCid(content.getCid());
			}
		}

		int a = categoryService.deleteCategory(cgid);
		if (a == 1) {
			map.put("data", "删除分类成功");
		} else {
			map.put("data", "删除分类失败");
		}
		return map;
	}

	@RequestMapping("/insertCategory")
	@ResponseBody
	public Map<String,Object> insertCategory(Category category){
		Map<String, Object> map = new HashMap<>();
		int a = categoryService.insertCategory(category);
		if (a == 1) {
			map.put("data", "增加分类成功");
		} else {
			map.put("data", "增加分类失败");
		}
		return map;
	}

	//导出分类数据
	@RequestMapping("/exportCategories")
	public void exportCategories(HttpServletResponse response) {
//		List<Category> categories = categoryService.selectAllCategory();
//		for (Category c : categories) {
//			c.setCount(contentService.selectContentCountByCgid(c.getCgid()));
//		}
//		try {
//			FileUtil.exportCategoriesToCsv(response, categories);
//		} catch (Exception e) {
//			// 处理异常
//		}

		List<Category> categories1 = categoryService.selectAllCategoryWithCount();
		try {
			FileUtil.exportCategoriesToCsv(response, categories1);
		} catch (Exception e) {
			// 处理异常
		}

	}



	// 导入分类数据
	@RequestMapping("/importCategories")
	@ResponseBody
	public Map<String,Object>  importCategories(@RequestParam("file") MultipartFile file) {

		Map<String, Object> map = new HashMap<>();

		if (file.isEmpty()) {
			map.put("data", "文件为空，请选择文件后上传");
		}

		try {
			// 这里可以编写文件解析和数据导入的逻辑
			// 比如将上传的 Excel 文件解析成数据，然后插入到数据库中
			InputStream inputStream = file.getInputStream();
			Workbook workbook = WorkbookFactory.create(inputStream);

			// 获取第一个工作表
			Sheet sheet = workbook.getSheetAt(0); // 假设数据在第一个工作表中
			for (Row row : sheet) {

				if (row.getRowNum() == 0) {
					continue;
				}
				String cgName = row.getCell(0).getStringCellValue(); // 第一列为分类名称
				String cgSlug = row.getCell(1).getStringCellValue(); // 第二列为分类标识

				System.out.println(cgName);
				System.out.println(cgSlug);

				Category category = new Category();
				category.setCgName(cgName);
				category.setCgSlug(cgSlug);

				categoryService.insertCategory(category);
			}

			workbook.close();
			inputStream.close();

			// 示例：假设直接返回导入成功
			map.put("data", "导入成功");
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			map.put("data", "导入失败，请检查文件格式或联系管理员，错误信息：" + e.getMessage());
			return map;
		}
	}
}
