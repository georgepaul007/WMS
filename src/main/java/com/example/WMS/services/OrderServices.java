package com.example.WMS.services;

import com.example.WMS.dtos.ListOfOrderDescription;
import com.example.WMS.dtos.OrderDescriptionDto;
import com.example.WMS.dtos.OrderDto;
import com.example.WMS.dtos.ValidationDto;

import java.util.List;

public interface OrderServices {
    ValidationDto createOrder(OrderDto orderDto);
    ListOfOrderDescription findOrder(String orderId);
    ListOfOrderDescription getAllOrder(String pageNo, String pageSize);
}
