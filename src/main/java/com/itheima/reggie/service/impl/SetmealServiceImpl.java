package com.itheima.reggie.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService{

	@Autowired
	private SetmealDishService setmealDishService;
	
	/**
	 * 删除套餐及套餐菜品对应关系
	 * @param ids
	 */
	@Transactional
	public void deleteWithDish(List<Long> ids) {
		
		// select count(*) from setmeal where id in {ids} and status = 1;
		// 判断要删除的套餐里有没有正在起售的
		LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
		setmealQueryWrapper.in(Setmeal::getId,ids);
		setmealQueryWrapper.eq(Setmeal::getStatus, 1);
		int count = this.count(setmealQueryWrapper);
		
		// 如果有正在起售的,抛出异常
		if (count > 0) {
			throw new CustomException("当前套餐起售中,无法删除");
		}
		
		// 如果可以,进行删除--setmeal
		this.removeByIds(ids);
		
		// 删除setmeal_dish中的关系数据
		LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.in(SetmealDish::getSetmealId, ids);
		setmealDishService.remove(queryWrapper);
	}
	/**
	 * 新增套餐信息及对应菜品
	 */
	@Transactional
	public void saveWithDish(SetmealDto setmealDto) {
		// 保存套餐信息
		this.save(setmealDto);
		
		// 获取套餐菜品信息
		List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
		// 设置套餐id
		Long setmealId = setmealDto.getId();
		for(int i = 0; i < setmealDishes.size() ; i++) {
			setmealDishes.get(i).setSetmealId(setmealId);
		}
		
		// 保存
		setmealDishService.saveBatch(setmealDishes);
	}
	

	/**
	 * 通过套餐id查询套餐信息及对应菜品信息
	 * @param id
	 * @return
	 */
	public SetmealDto getByIdWithDish(Long id) {
		
		// 查询setmeal表
		Setmeal setmeal = this.getById(id);
		
		// 查询setmealDish表
		LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(SetmealDish::getSetmealId, id);
		List<SetmealDish> dishes = setmealDishService.list(queryWrapper);
		
		SetmealDto setmealDto = new SetmealDto();
		BeanUtils.copyProperties(setmeal, setmealDto);
		setmealDto.setSetmealDishes(dishes);
		
		return setmealDto;
	}


	/**
	 * 更新套餐信息和对应菜品信息
	 * @param setmealDto
	 */
	@Transactional
	public void updateWithDish(SetmealDto setmealDto) {
		// 更新setmeal表
		this.updateById(setmealDto);
		
		// 更新setmeal_dish表
		// 先删除原有菜品
		LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
		setmealDishService.remove(lambdaQueryWrapper);
		
		// 设置setmeal_id
		List<SetmealDish> dishes = setmealDto.getSetmealDishes();
		Long setmealId = setmealDto.getId();
		for(int i = 0; i < dishes.size() ; i++) {
			dishes.get(i).setSetmealId(setmealId);
		}
		
		// 保存到setmeal_dish表
		setmealDishService.saveBatch(dishes);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
