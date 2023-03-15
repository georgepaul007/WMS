package com.example.wms.services;

import com.example.wms.dtos.ChangeQuantityDto;
import com.example.wms.dtos.ListOfOrderDescription;
import com.example.wms.dtos.ListOfOrderItem;
import com.example.wms.dtos.ValidationDto;

public interface OrderServices {
    ListOfOrderDescription findOrder(String orderId);
    ListOfOrderDescription getAllOrder(Integer pageNo, Integer pageSize);
    boolean completeOrder(ChangeQuantityDto changeQuantityDto);

    ListOfOrderDescription getByStatus(String status, Integer pageNo, Integer pageSize);

    void pickItem(String productName, String orderId, Integer quantity);
    ValidationDto createOrder(ListOfOrderItem listOfOrderItem);
}
