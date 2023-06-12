package com.itheima.reggie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;

import lombok.extern.slf4j.Slf4j;

/**
 * 分类管理
 * @author zhuwang
 *
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
	@Autowired
	private CategoryService categoryService;
	
	@DeleteMapping
	public R<String> delete(Long ids){
		
		categoryService.removeById(ids);
		
		return R.success("删除分类信息成功");
	}
	
	@GetMapping("/page")
	public R<Page<Category>> page(int page, int pageSize){
		log.info("page = {}, pageSize = {}",page,pageSize);
		
		// 创建分页构造器
		Page<Category> pageInfo = new Page<>(page,pageSize);
		
		// 创建条件构造器
		LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
		// 添加排序条件，按sort排序
		queryWrapper.orderByAsc(Category::getSort);
		
		// 进行分页查询
		categoryService.page(pageInfo, queryWrapper);
		
		// 返回结果
		return R.success(pageInfo);
	}
	
	
	/**
	 * 新增菜品分类、套餐分类
	 * @param category
	 * @return
	 */
	@PostMapping
	public R<String> save(@RequestBody Category category){
		
		categoryService.save(category);
		
		return R.success("新增分类成功");
	}
}
