package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

public interface DishService extends IService<Dish>{

	/**
	 * 新增菜品，同时保存口味
	 * @param dishDto
	 */
	public void saveWithFlavor(DishDto dishDto);
}
