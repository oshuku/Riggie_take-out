package com.itheima.reggie.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

	@Autowired
	private ShoppingCartService shoppingCartService;

	/**
	 * 购物车数量减一
	 * @param shoppingCart
	 * @return
	 */
	@PostMapping("/sub")
	public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
		Long currentId = BaseContext.getCurrentId();
		// 判断添加的是菜品还是套餐
		Long dishId = shoppingCart.getDishId();

		// Sql:select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
		LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ShoppingCart::getUserId, currentId);

		if (dishId != null) {
			// 如果添加的是菜品
			queryWrapper.eq(ShoppingCart::getDishId, dishId);
		} else {
			// 如果添加的套餐
			queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
		}
		
		ShoppingCart one = shoppingCartService.getOne(queryWrapper);
		
		if(one != null) {
			Integer number = one.getNumber();
			one.setNumber(number - 1);
			shoppingCartService.updateById(one);
			return R.success(one);
		}
		return R.error("错误");
	}

	/**
	 * 清空购物车
	 * @return
	 */
	@DeleteMapping("/clean")
	public R<String> clean() {
		LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
		shoppingCartService.remove(queryWrapper);
		return R.success("清空购物车成功");
	}

	/**
	 * 展示购物车
	 * @return
	 */
	@GetMapping("/list")
	public R<List<ShoppingCart>> list() {
		// 获取userId,通过id查询shopping_cart表
		LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
		List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
		return R.success(list);
	}

	/**
	 * 添加购物车
	 * @param shoppingCart
	 * @return
	 */
	@PostMapping("/add")
	public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
		log.info("shoppingCart = {}", shoppingCart);

		// 设置用户id
		Long currentId = BaseContext.getCurrentId();
		shoppingCart.setUserId(currentId);

		// 判断添加的是菜品还是套餐
		Long dishId = shoppingCart.getDishId();

		// Sql:select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
		LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ShoppingCart::getUserId, currentId);

		if (dishId != null) {
			// 如果添加的是菜品
			queryWrapper.eq(ShoppingCart::getDishId, dishId);
		} else {
			// 如果添加的套餐
			queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
		}

		ShoppingCart getShoppingCart = shoppingCartService.getOne(queryWrapper);

		// 判断是否改菜品/套餐是否已经在购物车中
		if (getShoppingCart != null) {
			// 如果存在,数量+1
			Integer number = getShoppingCart.getNumber();
			getShoppingCart.setNumber(number + 1);
			shoppingCartService.updateById(getShoppingCart);
		} else {
			// 如果不存在,直接添加
			shoppingCart.setNumber(1);
			shoppingCart.setCreateTime(LocalDateTime.now());
			shoppingCartService.save(shoppingCart);
			getShoppingCart = shoppingCart;
		}

		return R.success(getShoppingCart);
	}

}
