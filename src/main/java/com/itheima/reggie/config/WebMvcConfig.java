package com.itheima.reggie.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration //添加Configuration注释，表示这个类是个配置类
public class WebMvcConfig extends WebMvcConfigurationSupport{
	/**
	 * 设置静态资源映射
	 */
	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		log.info("开始静态资源映射...");
		
		//classpath就是source目录，建立映射关系
		registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
		registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
	}
}
