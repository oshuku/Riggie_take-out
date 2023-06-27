package com.itheima.reggie.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.common.ValidateCodeUtils;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

	@Autowired
	private UserService userService;

	/**
	 * 手机端用户登录
	 * @return
	 */
	@PostMapping("/login")
	public R<User> login(@RequestBody Map map, HttpSession session) { // 前端传来{"phone":"bcdcd123@gmail.com","code":"1234"},可以用map接收
		// 获取邮箱
		String phone = map.get("phone").toString();
		// 获取验证码
		String code = map.get("code").toString();
		log.info("map={}",map);

		// 如果验证码不为空

		// 校验验证码
		String codeInSession = session.getAttribute(phone).toString();

		if (StringUtils.isNotEmpty(codeInSession) && codeInSession.equals(code)) {
			LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
			lambdaQueryWrapper.eq(User::getPhone, phone);
			User user = userService.getOne(lambdaQueryWrapper);

			// 判断是否是新用户
			if (user == null) {
				// 如果是新用户保存到user表
				user = new User();
				user.setPhone(phone);
				user.setStatus(1);
				userService.save(user);

			}

			// userId保存到session中,使过滤器允许通行
			session.setAttribute("user", user.getId());

			return R.success(user);
		}

		return R.error("登录失败");
	}

	/**
	 * 手机端用户登录时向邮箱发送验证码
	 * @param user
	 * @param session
	 * @return
	 */
	@PostMapping("/sendMsg")
	public R<String> sendMsg(@RequestBody User user, HttpSession session) {

		// 获取邮箱
		String phone = user.getPhone();
		log.info("phone:{}", phone);

		// 判断邮箱是否为空
		if (StringUtils.isNotEmpty(phone)) {
			// 生成验证码
			String code = ValidateCodeUtils.generateValidateCode(4).toString();
			log.info("code={}", code);

			// 设置邮件主题
			String subject = "瑞吉餐购登录验证码";

			// 设置邮件模板
			String context = "欢迎使用瑞吉餐购，登录验证码为: " + code + ",五分钟内有效，请妥善保管!";

			// 发送验证码
			userService.sendMsg(phone, subject, context);

			// 在session中保存phone-code
			session.setAttribute(phone, code);

			return R.success("验证码发送成功");
		}

		return R.error("验证码发送失败");
	}

}
