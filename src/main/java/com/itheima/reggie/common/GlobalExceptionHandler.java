package com.itheima.reggie.common;

import java.sql.SQLIntegrityConstraintViolationException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

//对带有RestController和Controller注解的Controller进行拦截
@ControllerAdvice(annotations = {RestController.class,Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
	
	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
		log.info(ex.getMessage());
		
		//判断错误信息里是否包含Duplicate entry
		if(ex.getMessage().contains("Duplicate entry")) {
			//使用空格分隔信息，取出username
			String[] messages = ex.getMessage().split(" ");
			String msg = messages[2] + "已存在";
			return R.error(msg);
		}
		
		
		return R.error("未知错误");
	}
}
