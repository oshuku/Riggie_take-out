package com.itheima.reggie.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.AntPathMatcher;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;

import lombok.extern.slf4j.Slf4j;

/**
 * 判断用户是否登录
 * @author zhuwang
 *
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
	// 路径匹配器，支持通配符
	public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		//1 获取本次请求url路径
		String requestURI = httpRequest.getRequestURI();

		log.info("拦截到请求：{}", requestURI);

		//创建集合，以下集合内不处理
		String[] urls = new String[] {
				"/employee/login",
				"/employee/logout",
				"/backend/**",
				"/front/**",
				"/common/**",
				"/user/login",
				"/user/sendMsg"
		};

		//2 判断本次请求是否需要处理
		boolean check = check(urls, requestURI);

		//3 如果不需要处理，则直接放行
		if (check) {
			log.info("本次请求无需处理");
			chain.doFilter(httpRequest, httpResponse);
			return;
		}

		//4-1 判断员工登录状态，已登录则直接放行
		if (httpRequest.getSession().getAttribute("employee") != null) {

			//获取session里的id存放到ThreadLocal里
			Long empId = (Long) httpRequest.getSession().getAttribute("employee");
			BaseContext.setCurrentId(empId);

			chain.doFilter(httpRequest, httpResponse);
			return;
		}

		//4-2 判断用户登录状态，已登录则直接放行
		if (httpRequest.getSession().getAttribute("user") != null) {

			//获取session里的id存放到ThreadLocal里
			Long userId = (Long) httpRequest.getSession().getAttribute("user");
			BaseContext.setCurrentId(userId);

			chain.doFilter(httpRequest, httpResponse);
			return;
		}

		//5 如未登录则返回未登录结果,通过输出流的方式向客户端页面响应数据
		log.info("用户未登录");
		response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
		return;

	}

	/**
	 * 路径匹配，检查本次请求是否需要直接放行
	 * @param urls
	 * @param requestURI
	 * @return
	 */
	public boolean check(String[] urls, String requestURI) {
		for (String url : urls) {
			boolean match = PATH_MATCHER.match(url, requestURI);
			if (match) {
				return true;
			}
		}
		return false;
	}

}
