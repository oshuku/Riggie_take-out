package com.itheima.reggie.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

public interface SetmealService extends IService<Setmeal>{
	
	/**
	 * 新增套餐信息及对应菜品
	 * @param setmealDto
	 */
	public void saveWithDish(SetmealDto setmealDto);
	
	
	/**
	 * 通过套餐id查询套餐信息及对应菜品信息
	 * @param id
	 * @return
	 */
	public SetmealDto getByIdWithDish(Long id);

	/**
	 * 更新套餐信息和对应菜品信息
	 * @param setmealDto
	 */
	public void updateWithDish(SetmealDto setmealDto);

	/**
	 * 删除套餐及套餐菜品对应关系
	 * @param ids
	 */
	public void deleteWithDish(List<Long> ids);
	
}
