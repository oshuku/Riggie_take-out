package com.itheima.reggie.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

	@Autowired
	private SetmealService	setmealService;
	
	@Autowired
	private CategoryService categoryService;
	
	/**
	 * 客户端查询套餐信息
	 * @param setmeal
	 * @return
	 */
	@GetMapping("/list")
	public R<List<Setmeal>> list(Setmeal setmeal) {
		LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(setmeal != null, Setmeal::getCategoryId, setmeal.getCategoryId());
		queryWrapper.eq(setmeal != null, Setmeal::getStatus, setmeal.getStatus());
		queryWrapper.orderByDesc(Setmeal::getUpdateTime);
		
		List<Setmeal> list = setmealService.list(queryWrapper);
		return R.success(list);
	}
	
	/**
	 * 删除套餐
	 * @param ids
	 * @return
	 */
	@DeleteMapping
	public R<String> delete(@RequestParam List<Long> ids){
		setmealService.deleteWithDish(ids);
		return R.success("删除套餐信息成功");
	}
	
	/**
	 * 套餐停售/起售
	 * @param status
	 * @param ids
	 * @return
	 */
	@PostMapping("/status/{status}")
	public R<String> updateStatus(@PathVariable Integer status, @RequestParam List<Long> ids){
		
		LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.in(ids != null, Setmeal::getId, ids);
		List<Setmeal> list = setmealService.list(queryWrapper);
		
		for(Setmeal setmeal : list) {
			setmeal.setStatus(status);
			setmealService.updateById(setmeal);
		}
		return R.success("售卖状态更新成功");
	}
	/**
	 * 修改套餐信息
	 * @param setmealDto
	 * @return
	 */
	@PutMapping
	public R<String> update(@RequestBody SetmealDto setmealDto){
		log.info("套餐信息：{}", setmealDto);
		setmealService.updateWithDish(setmealDto);
		return R.success("修改套餐信息成功");
	}
	
	/**
	 * 修改套餐信息回显
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	public R<SetmealDto> getById(@PathVariable Long id){
		log.info("id = {}",id);
		
		SetmealDto setmealDto = setmealService.getByIdWithDish(id);
		return R.success(setmealDto);
	}
	
	/**
	 * 分页查询
	 * @param page
	 * @param pageSize
	 * @param name
	 * @return
	 */
	@GetMapping("/page")
	public R<Page<SetmealDto>> page(int page, int pageSize,String name){
		Page<Setmeal> pageInfo = new Page<>(page,pageSize);
		Page<SetmealDto> pageSetmealDto = new Page<>();
		
		LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.like(name != null, Setmeal::getName, name);
		queryWrapper.orderByDesc(Setmeal::getUpdateTime);
		
		setmealService.page(pageInfo, queryWrapper);
		BeanUtils.copyProperties(pageInfo, pageSetmealDto,"records");
		
		List<Setmeal> setmealRecords = pageInfo.getRecords();
		List<SetmealDto> list = new ArrayList<>();
		
		for(int i = 0 ; i < setmealRecords.size() ; i++) {
			Setmeal setmeal = setmealRecords.get(i);
			SetmealDto SetmealDto = new SetmealDto();
			BeanUtils.copyProperties(setmeal, SetmealDto);
			
			Long categoryId = setmeal.getCategoryId();
			Category category = categoryService.getById(categoryId);
			
			if(category != null) {
				String categoryName = category.getName();
				SetmealDto.setCategoryName(categoryName);
			}
			
			
			list.add(SetmealDto);
			
			
		}
		
		pageSetmealDto.setRecords(list);
		return R.success(pageSetmealDto);
	}
	
	/**
	 * 新增套餐
	 * @param setmealDto
	 * @return
	 */
	@PostMapping
	public R<String> save(@RequestBody SetmealDto setmealDto){
		log.info("setmealDto = {}", setmealDto);
		
		setmealService.saveWithDish(setmealDto);
		return R.success("保存套餐信息成功");
	}
}
