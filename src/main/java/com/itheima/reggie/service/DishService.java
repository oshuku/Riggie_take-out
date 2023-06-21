package com.itheima.reggie.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

public interface DishService extends IService<Dish>{

	/**
	 * 新增菜品，同时保存口味
	 * @param dishDto
	 */
	public void saveWithFlavor(DishDto dishDto);
	
	
	/**
	 * 根据id查询菜品信息和对应的口味信息
	 * @param id
	 */
	public DishDto getWithFlavorById(Long id);
	
	/**
	 * 更新菜品信息和口味
	 * @param dishDto
	 */
	public void updateWithFlavor(DishDto dishDto);
	
	/**
	 * 删除菜品信息及对应口味
	 * @param ids
	 */
	public void deleteByIdWithFlavor(List<Long> ids);
}
