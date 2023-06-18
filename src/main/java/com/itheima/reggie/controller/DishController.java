package com.itheima.reggie.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
	
	@Autowired
	private DishService dishService;
	
	@Autowired
	private CategoryService categoryService;
	
	/**
	 * 分页展示菜品
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/page")
	public R<Page<DishDto>> page(int page,int pageSize,String name){
		// 创建分页构造器
		Page<Dish> pageInfo = new Page<>(page,pageSize);
		Page<DishDto> dishDtoPage = new Page<>();
		
		// 创建条件构造器
		LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
		// 添加过滤条件
		queryWrapper.like(name != null, Dish::getName, name);
		// 添加排序条件
		queryWrapper.orderByDesc(Dish::getUpdateTime);
		
		// 分页查询
		dishService.page(pageInfo, queryWrapper);
		
		// 将结果pageInfo复制给dishDtoPage，除了records
		BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
		
		// 对pageInfo的records进行处理
		// 获得pageInfo的records
		List<Dish> records = pageInfo.getRecords();
		
		// 准备一个dishDto的空list，用来存放复制的pageInfo的records
		List<DishDto> dishDtoRecords = new ArrayList<>(); 
		
		// 循环遍历pageInfo的records
		for(int i = 0; i < records.size(); i++) {
			// 获得records列表里的dish对象
			Dish dish = records.get(i);
			
			// 新建一个空dishDto对象，用来存放复制的dish对象里的信息
			DishDto dishDto = new DishDto();
			
			// 开始复制
			BeanUtils.copyProperties(dish, dishDto);
			
			// 获得dish对象里的分类id
			Long categoryId = dish.getCategoryId();
			// 通过分类id查询分类名
			Category category = categoryService.getById(categoryId);
			
			if(category != null) {
				
				String categoryName = category.getName();
				// 将查询到的分类名设置给dishDto对象
				dishDto.setCategoryName(categoryName);
			}
			
			// 将dishDto对象存放到列表中
			dishDtoRecords.add(dishDto);
		}
		
		// 将dishDtoRecords设置为dishPage的records
		dishDtoPage.setRecords(dishDtoRecords);
		
		return R.success(dishDtoPage);
	}

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
