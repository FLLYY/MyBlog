package com.rawchen.controller;

import com.rawchen.domain.User;
import com.rawchen.domain.dto.EmailDTO;
import com.rawchen.utils.GravatarUtil;
import com.rawchen.utils.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController extends BaseController {

	/**
	 * 首页跳转Login页面
	 * @param model
	 * @return
	 */
	@RequestMapping("/login")
	public String toLogin(Model model) {
		return "login";
	}

	/**
	 * 跳转Register页面
	 * @param model
	 * @return
	 */
	@RequestMapping("/register")
	public String toRegister(Model model) {
		return "register";
	}

	/**
	 * 注销跳转登录
	 * @return
	 */
	@RequestMapping("/logout")
	public String toLogout(Model model) {
		session.invalidate();
		return "redirect:/";
	}

	/**
	 * 通过ajax登录系统后台
	 * @param u
	 * @param remember
	 * @return
	 */
	@RequestMapping("/userLogin")
	@ResponseBody
	public Map<String,Object> userLogin(User u,
			@RequestParam(value = "remember", required = false) String remember,
			HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> map = new HashMap<>();
		User user = userService.selectUserByNameAndPassword(u);
		if (user == null) {
			map.put("data","error");
		} else {
			map.put("data","success");
			session.setAttribute("USER_SESSION", user);
			if ("on".equals(remember)) {
				Cookie cookie = new Cookie("JSESSIONID", req.getSession().getId());
				cookie.setMaxAge(60 * 60 * 24 * 7);
				session.setMaxInactiveInterval(60 * 60 * 24 * 7);
				resp.addCookie(cookie);
			}
		}
		return map;
	}

	/**
	 * 通过ajax注册
	 * @param u
	 * @return
	 */
	@RequestMapping("/userRegister")
	@ResponseBody
	public Map<String,Object> userRegister(User u) {
		User user = u;
		Map<String, Object> map = new HashMap<>();
		String pwd = u.getPassword();
		user.setPassword(StringUtil.passwordToMd5(pwd));
		user.setCreated(new Date());
		user.setScreenname(u.getName());
		user.setActivated(new Date());
		user.setRole("user");
		user.setPhoto(GravatarUtil.getGravatarUrlByEmail(u.getMail()));
		int value = userService.insert(u);
		if (value == 0) {
			map.put("data","error");
		} else {
			map.put("data","success");
		}
		return map;
	}

	/**
	 * 通过用户名查找用户
	 * @param userName
	 * @return
	 */
	@RequestMapping("/findUserByUserName")
	@ResponseBody
	public Map<String,Object> findUserByUserName(@RequestParam(value="userName")String userName){
		Map<String, Object> map = new HashMap<>();
		User user = userService.findUserByUserName(userName);
		boolean userExist = false;
		if (user != null) {
			userExist = true;
		}
		map.put("userExist", userExist);
		return map;
	}

	/**
	 * 通过邮箱判断是否合法
	 * @param email
	 * @return
	 */
	@RequestMapping("/isEmail")
	@ResponseBody
	public Map<String, Object> isEmail(@RequestParam("email") String email) {
		Map<String, Object> response = new HashMap<>();
		boolean isValid = isValidEmailAddress(email);

		response.put("isEmail", isValid);
		if (!isValid) {
			response.put("message", "邮箱格式不正确");
		}

		return response;
	}


	private boolean isValidEmailAddress(String email) {
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
			return true;
		} catch (AddressException ex) {
			return false;
		}
	}

	/**
	 * 通过邮箱判断是否合法
	 * @param emailDTO
	 * @param bindingResult
	 * @return
	 */
	@GetMapping("/isEmail1")
	@ResponseBody
	public Map<String, Object> isEmail1(
			@Valid EmailDTO emailDTO,
			BindingResult bindingResult) {

		Map<String, Object> response = new HashMap<>();

		if (bindingResult.hasErrors()) {
			response.put("isEmail", false);
			response.put("message", bindingResult.getAllErrors().get(0).getDefaultMessage());
		} else {
			response.put("isEmail", true);
			response.put("message", "Email is valid");
		}

		return response;
	}


	@RequestMapping("/updateUserInfo")
	public String updateUserInfo() {
		String screenName = request.getParameter("screenName");
		String email = request.getParameter("email");
		String url = request.getParameter("url");
		User u = (User) session.getAttribute("USER_SESSION");
		u.setScreenname(screenName);
		u.setMail(email);
		u.setPhoto(GravatarUtil.getGravatarUrlByEmail(email));
		u.setUrl(url);
		int a = userService.updateScreenNameAndMailAndUrl(u);

		return "redirect:/adminConfig";
	}

	@RequestMapping("/updateUserPassword")
	@ResponseBody
	public Map<String,Object> updateUserPassword(String oldPwd,String newPwd) {
		Map<String, Object> map = new HashMap<>();
		String newOldPwd = StringUtil.passwordToMd5(oldPwd);
		User u = (User) session.getAttribute("USER_SESSION");
		if (u.getPassword().equals(newOldPwd)) {
			u.setPassword(StringUtil.passwordToMd5(newPwd));
			int a = userService.updatePassword(u);
			if (a == 1) {
				map.put("data", "success");
			} else {
				map.put("data", "fail");
			}
		} else {
			map.put("data", "pwdfail");
		}
		return map;
	}

	@RequestMapping("/deleteUser/{uid}")
	public String deleteUser(@PathVariable int uid) {
		int a = userService.deleteByPrimaryKey(uid);
		return "redirect:/adminUser";
	}

	@RequestMapping("/deleteSelectUser")
	public String deleteSelectUser() {
		String[] uids = request.getParameterValues("uid");
		int a = userService.deleteSelectUser(uids);
		return "redirect:/adminUser";
	}

	/**
	 * 获取用户列表
	 * @return
	 */
	@RequestMapping("/adminGetUserList")
	@ResponseBody
	public Map<String,Object> adminGetUserList() {
		Map<String, Object> map = new HashMap<>();
		List<User> users = userService.selectAllUser();
		map.put("data",users);
		return map;
	}
}
