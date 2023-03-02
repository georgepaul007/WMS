package com.example.wms.services;

import com.example.wms.dtos.AddIGDto;
import com.example.wms.dtos.ListOfOrderDescription;
import com.example.wms.dtos.ValidationDto;

public interface OrderServices {
    ValidationDto createOrder(Integer quantity, String name);
    ListOfOrderDescription findOrder(String orderId);
    ListOfOrderDescription getAllOrder(Integer pageNo, Integer pageSize);
    void completeOrder(AddIGDto addIGDto);
}
