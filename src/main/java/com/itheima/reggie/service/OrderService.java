package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Orders;

public interface OrderService extends IService<Orders>{
	
	/**
	 * 提交订单
	 * @param orders
	 */
	public void submit(Orders orders);

}
