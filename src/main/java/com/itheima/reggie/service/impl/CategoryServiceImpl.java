package com.itheima.reggie.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{

	
	@Autowired
	private DishService dishService;
	
	@Autowired
	private SetmealService setmealService;
	/**
	 * 删除分类，删除前需要判断是否有菜品和套餐与该分类有关联
	 */
	@Override
	public void remove(Long id) {
		//添加查询条件，根据id进行查询
		LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
		dishQueryWrapper.eq(Dish::getCategoryId, id);
		
		//查询当前分类是否关联了菜品，如果已关联，抛出一个业务异常
		int count = dishService.count(dishQueryWrapper);
		
		if(count > 0) {
			//有菜品与该分类已有关联,抛业务异常
			throw new CustomException("当前分类下关联了菜品，不能删除");
		}
		
		//添加查询条件，根据id进行查询
		LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
		setmealQueryWrapper.eq(Setmeal::getCategoryId,id);
		
		//查询当前分类是否关联了套餐，如果已关联，抛出一个业务异常
		int count2 = setmealService.count(setmealQueryWrapper);
		
		if(count2 > 0) {
			//有套餐与该分类已有关联,抛业务异常
			throw new CustomException("当前分类下关联了套餐，不能删除");
		}
		
		
		// 正常删除
		super.removeById(id);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
