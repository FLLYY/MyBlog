package com.rawchen.controller;

import com.rawchen.domain.Content;
import com.rawchen.domain.MyFile;
import com.rawchen.domain.User;
import com.rawchen.utils.FileUtil;
import com.rawchen.utils.LogUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FileController extends BaseController {

	@RequestMapping("/file")
	public String showFiles(Model model) {

		//插入日志
		logService.insert(LogUtil.insertLog(request,"/file"));

		List<Content> contents = contentService.selectAllContent();
		for (Content c : contents) {
			c.setCategorySlug(categoryService.selectByPrimaryKey(c.getCgid()).getCgSlug());
		}

		List<MyFile> files = fileService.selectAllFile();
		model.addAttribute("files",files);

		model.addAttribute("contents",contents);

		List<Content> recommendContents = contentService.selectRecommendContent();
		model.addAttribute("recommendContents",recommendContents);
		return "file";

	}

	/**
	 * 跳转file-mgr.html页面
	 * @param model
	 * @return
	 */
	@RequestMapping("/adminFile")
	public String adminUser(Model model) {
		return "file-mgr";
	}


	@ResponseBody
	@RequestMapping("/uploadFileList")
	public Map<String,Object> uploadFileList(HttpServletRequest req, @RequestParam(value = "files[]") MultipartFile[] files) {
		Map<String, Object> map = new HashMap<>();

		User user = (User) session.getAttribute("USER_SESSION");
		int uid = user.getUid();

		String url = "file/";
		String realPath = req.getServletContext().getRealPath("/upload/");
		java.io.File file = new java.io.File(realPath, url);
		if (!file.exists() && file.mkdirs()) {
		}
		map.put("success", 1);

		for (MultipartFile mFile : files) {
			try {
				String fileName = mFile.getOriginalFilename();

				// 其中一个文件存在同名
				if (new File(realPath, url + fileName).exists()) {
					map.put("success", -1);
					map.put("message", "文件已存在");
					break;
				}

				// 后缀
				if (fileName != null && !fileName.contains(".")) {
					map.put("success", 0);
					map.put("message", "文件无法识别后缀");
					break;
				}

				// 限制文件类型
				if (fileName != null && (fileName.toLowerCase().endsWith(".jsp") || fileName.toLowerCase().endsWith(".asp"))) {
					map.put("success", 0);
					map.put("message", "无法上传类型为jsp和asp的文件");
					break;
				}

				MyFile newFile = new MyFile();
				newFile.setPath("/upload/file/"+fileName);
				newFile.setAuthorId(uid);
				newFile.setFileStatus("publish");
				newFile.setName(fileName);

				//修正上传判断
				mFile.transferTo(new java.io.File(realPath,url + fileName));

				newFile.setFileType(FileUtil.imageType(fileName.substring(fileName.lastIndexOf(".")+1)));
				newFile.setCreatedTime(new Date());
				newFile.setModifiedTime(new Date());
				newFile.setDownloadCount(0);
				newFile.setFileSize(FileUtil.formatBytes(mFile.getSize()));
				fileService.insert(newFile);

				map.put("message", "上传成功");

			} catch (Exception e) {
				e.printStackTrace();
				map.put("success", 0);
				map.put("message", "上传失败");
				return map;
			}
		}
		return map;
	}

	@RequestMapping("/deleteFile/{fid}")
	public String deleteFile(@PathVariable int fid, HttpServletRequest req) {
		User user = (User) session.getAttribute("USER_SESSION");

		String url = "file/";
		String realPath = req.getServletContext().getRealPath("/upload/");
		java.io.File file1 = new java.io.File(realPath, url + fileService.selectFileByFid(fid).getName());
		if (file1.exists()) {
			file1.delete();
		}
		int a = fileService.deleteByPrimaryKey(fid);
		return "redirect:/adminFile";
	}

	@RequestMapping("/deleteSelectFile")
	public String deleteSelectFile(HttpServletRequest req) {
		String[] fids = request.getParameterValues("fid");
		try {
			if (fids != null && fids.length > 0) {
				for (String fid: fids) {
					String url = "file/";
					String realPath = req.getServletContext().getRealPath("/upload/");
					java.io.File file1 = new java.io.File(realPath, url + fileService.selectFileByFid(Integer.parseInt(fid)).getName());
					if (file1.exists()) {
						file1.delete();
					}
					int a = fileService.deleteByPrimaryKey(Integer.parseInt(fid));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/adminFile";
	}

	/**
	 * admin获取文件列表
	 * @return
	 */
	@RequestMapping("/adminGetFileList")
	@ResponseBody
	public Map<String,Object> adminGetFileList() {
		Map<String, Object> map = new HashMap<>();
		List<MyFile> files = fileService.selectAllFile();
		map.put("data",files);
		return map;
	}

	/**
	 * user获取文件列表
	 * @param userId
	 * @return
	 */
	@RequestMapping("/userGetFileList")
	@ResponseBody
	public Map<String,Object> userGetFileList(@RequestParam(value="userId") int userId) {
		Map<String, Object> map = new HashMap<>();
		List<MyFile> files = fileService.selectFileListWithUid(userId);
		map.put("data",files);
		return map;
	}

	@RequestMapping("/downloadFile/{fid}")
	public ResponseEntity<byte[]> downloadFile(@PathVariable int fid, HttpServletRequest request) throws IOException
	{
		System.out.println("hanhan");
		// 获取文件路径
		String filePath = "/upload/file/";  // 文件存储路径
		String fileName = fileService.selectFileByFid(fid).getName();  // 获取文件名，你的实现中可能不同

		// 编码文件名，确保其中的特殊字符能够正确编码
		String encodedFileName = URLEncoder.encode(fileName, "UTF-8");

		// 获取文件的真实路径
		ServletContext context = request.getServletContext();
		String realPath = context.getRealPath(filePath);
		String fullPath = realPath + fileName;

		// 构建文件对象
		File file = new File(fullPath);

		// 如果文件存在，则进行下载
		if (file.exists())
		{
			// 构建响应头
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDispositionFormData("attachment", encodedFileName);

			// 读取文件内容并返回
			InputStream inputStream = new FileInputStream(file);
			byte[] bytes = new byte[inputStream.available()];
			inputStream.read(bytes);
			inputStream.close();

			return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
		} else
		{
			// 文件不存在时返回404
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
