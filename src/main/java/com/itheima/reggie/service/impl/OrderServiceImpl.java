package com.itheima.reggie.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.mapper.OrderMapper;
import com.itheima.reggie.service.AddressBookService;
import com.itheima.reggie.service.OrderDetailService;
import com.itheima.reggie.service.OrderService;
import com.itheima.reggie.service.ShoppingCartService;
import com.itheima.reggie.service.UserService;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

	@Autowired
	private OrderDetailService orderDetailService;

	@Autowired
	private ShoppingCartService shoppingCartService;

	@Autowired
	private UserService userService;

	@Autowired
	private AddressBookService addressBookService;

	/**
	 * 提交订单
	 */
	@Transactional
	public void submit(Orders orders) {
		// 获取当前用户id
		Long currentId = BaseContext.getCurrentId();

		// 使用userId查询shopping_cart表
		LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ShoppingCart::getUserId, currentId);
		List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);

		if (shoppingCarts == null || shoppingCarts.size() == 0) {
			throw new CustomException("购物车为空，不能下单");
		}

		// 查询用户数据
		User user = userService.getById(currentId);

		// 查询地址数据
		AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
		if (addressBook == null) {
			throw new CustomException("地址信息有误，不能下单");
		}

		long orderId = IdWorker.getId(); // 订单号

		AtomicInteger amount = new AtomicInteger(0);

		// 将shoppingcart转化为orderdetail
		List<OrderDetail> orderDetails = shoppingCarts.stream().map((shoppingCart) -> {
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setOrderId(orderId);
			orderDetail.setNumber(shoppingCart.getNumber());
			orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
			orderDetail.setDishId(shoppingCart.getDishId());
			orderDetail.setSetmealId(shoppingCart.getSetmealId());
			orderDetail.setName(shoppingCart.getName());
			orderDetail.setImage(shoppingCart.getImage());
			orderDetail.setAmount(shoppingCart.getAmount());

			// shoppingcart中的amount*number=orders中的amount总价
			amount.addAndGet(shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber())).intValue());
			return orderDetail;
		}).collect(Collectors.toList());

		orders.setId(orderId);
		orders.setOrderTime(LocalDateTime.now());
		orders.setCheckoutTime(LocalDateTime.now());
		orders.setStatus(2);
		orders.setAmount(new BigDecimal(amount.get()));//总金额
		orders.setUserId(currentId);
		orders.setNumber(String.valueOf(orderId));
		orders.setUserName(user.getName());
		orders.setConsignee(addressBook.getConsignee());
		orders.setPhone(addressBook.getPhone());
		orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
				+ (addressBook.getCityName() == null ? "" : addressBook.getCityName())
				+ (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
				+ (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
		
		// 将orders保存到orders表
		this.save(orders);
		
		//向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartService.remove(queryWrapper);

	}

}
