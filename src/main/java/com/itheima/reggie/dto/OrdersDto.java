package com.itheima.reggie.dto;

import java.util.List;

import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;

import lombok.Data;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
