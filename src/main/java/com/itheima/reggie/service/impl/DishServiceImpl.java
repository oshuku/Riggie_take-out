package com.itheima.reggie.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService{
	
	@Autowired
	private DishFlavorService dishFlavorService;

	/**
	 * 新增菜品，同时保存口味。操作两张表
	 */
	@Transactional // 涉及两张表，开启事务。启动类上也要做事务配置
	public void saveWithFlavor(DishDto dishDto) {
		// 保存菜品信息到dish表
		this.save(dishDto);
		
		// 保存口味到dish_flavor表
		// 获取口味的列表，但此时列表里并没有该口味对应的菜品的id
		List<DishFlavor> flavors = dishDto.getFlavors();
		
		// 获取菜品id。因为MP是先自动生成id保存到实体之后在保存到数据库，所以可以直接通过实体获取id
		Long dishId = dishDto.getId();
		
		// 利用循环，为list表里的每个口味设置上菜品Id
		for(int i = 0; i < flavors.size(); i++) {
			flavors.get(i).setDishId(dishId);;
		}
		
		// 保存菜品口味数据到菜品口味表dish_flavor
		dishFlavorService.saveBatch(flavors);
	}

	
}
