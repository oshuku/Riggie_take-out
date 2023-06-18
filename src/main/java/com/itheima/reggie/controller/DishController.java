package com.itheima.reggie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.service.DishService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
	
	@Autowired
	private DishService dishService;

	/**
	 * 新增菜品
	 * @param dishDto
	 * @return
	 */
	@PostMapping
	public R<String> save(@RequestBody DishDto dishDto){
		log.info(dishDto.toString());
		
		dishService.saveWithFlavor(dishDto);
		
		return R.success("新增菜品成功");
	}
}
