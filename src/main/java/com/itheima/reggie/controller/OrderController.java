package com.itheima.reggie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrderService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	/**
	 * 写入订单及订单详细
	 * @return
	 */
	@PostMapping("/submit")
	public R<String> submit(@RequestBody Orders orders){
		log.info("订单信息：{}", orders);
		
		orderService.submit(orders);
		return R.success("下單成功");
	}

	
	
	
	
	
	
}
