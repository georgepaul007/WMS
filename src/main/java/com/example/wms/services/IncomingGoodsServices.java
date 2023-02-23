package com.example.wms.services;


import com.example.wms.dtos.ListOfAddStock;
import com.example.wms.dtos.ValidationDto;

public interface IncomingGoodsServices {
    ValidationDto createIncomingGoods(Integer quantity, String name);
    ListOfAddStock findIncomingGoods(String incomingGoodsId);
    ListOfAddStock getAllIncomingGoods(Integer pageNo, Integer pageSize);
}
