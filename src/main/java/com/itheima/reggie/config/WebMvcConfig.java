package com.itheima.reggie.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.itheima.reggie.common.JacksonObjectMapper;

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
	
	/**
	 * 拓展MVC框架的消息转换器
	 */
	@Override
	protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		// 创建消息转换器
		MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter(); 
		// 设置对象转换器，底层使用Jackson将Java对象转为json
		messageConverter.setObjectMapper(new JacksonObjectMapper());
		// 将上面的消息转换器对象追加到mvc框架的转换器集合中，注意索引设置为0
		converters.add(0, messageConverter);
	}
}
