package com.example.wms.services;

import com.example.wms.dtos.AddIGDto;
import com.example.wms.dtos.ListOfOrderDescription;

public interface OrderServices {
//    ValidationDto createOrder(Integer quantity, String name);
    ListOfOrderDescription findOrder(String orderId);
    ListOfOrderDescription getAllOrder(Integer pageNo, Integer pageSize);
    void completeOrder(AddIGDto addIGDto);

    ListOfOrderDescription getByStatus(String status, Integer pageNo, Integer pageSize);
}
