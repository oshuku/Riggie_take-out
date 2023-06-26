package com.itheima.reggie.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
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
	 * 根据id查询菜品信息和对应的口味信息
	 */
	public DishDto getWithFlavorById(Long id) {
		
		// 查询菜品基本信息，从地dish查询
		Dish dish = this.getById(id);
		
		// 查询当前菜品对应的口味信息，从dish_flavor查询
		LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = new LambdaQueryWrapper<>();
		dishFlavorQueryWrapper.eq(id != null, DishFlavor::getDishId, id);
		List<DishFlavor> flavors = dishFlavorService.list(dishFlavorQueryWrapper);
		
		// 拷贝
		DishDto dishDto = new DishDto();
		BeanUtils.copyProperties(dish, dishDto);
		dishDto.setFlavors(flavors);
		
		return dishDto;
	}




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
		/*for(int i = 0; i < flavors.size(); i++) {
			flavors.get(i).setDishId(dishId);;
		}*/
		
		// 使用Lambda
		flavors.stream().map((item) -> {
			item.setDishId(dishId);
			return item;
		}).collect(Collectors.toList());
		
		
		// 保存菜品口味数据到菜品口味表dish_flavor
		
		dishFlavorService.saveBatch(flavors);
	}



	/**
	 * 更新菜品信息和口味信息
	 */
	@Transactional
	public void updateWithFlavor(DishDto dishDto) {
		// 根据id更新菜品信息到dish表
		this.updateById(dishDto);
		
		
		// 清理当前菜品对应口味数据---dish_flavor表的delete操作
		LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
		dishFlavorService.remove(queryWrapper);
		
		// 添加当前提交过来的口味数据---dish_flavor表的insert操作
		Long dishId = dishDto.getId();
		List<DishFlavor> flavors = dishDto.getFlavors();
		/*for(int i = 0 ; i < flavors.size(); i++) {
			flavors.get(i).setDishId(dishId);
		}*/
		
		flavors.stream().map((item) -> {
			item.setDishId(dishId);
			return item;
		}).collect(Collectors.toList());
		
		// 保存菜品口味数据到菜品口味表dish_flavor
		dishFlavorService.saveBatch(flavors);
	}



	/**
	 * 删除菜品信息和口味信息
	 */
	@Transactional
	public void deleteByIdWithFlavor(List<Long> ids) {
		
		// 判断要删除的菜品是否在售
		LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
		dishQueryWrapper.in(Dish::getId, ids);
		dishQueryWrapper.eq(Dish::getStatus, 1);
		int count = this.count(dishQueryWrapper);
		
		// 存在起售菜品,抛出异常
		if (count > 0) {
			throw new CustomException("当前菜品起售中，无法删除");
		}
		
		// 删除菜品
		this.removeByIds(ids);
		
		// 删除菜品口味
		LambdaQueryWrapper<DishFlavor> flavorQueryWrapper = new LambdaQueryWrapper<>();
		flavorQueryWrapper.in(DishFlavor::getDishId, ids);
		dishFlavorService.remove(flavorQueryWrapper);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
