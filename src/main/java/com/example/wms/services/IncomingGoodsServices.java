package com.example.wms.services;


import com.example.wms.dtos.AddIGDto;
import com.example.wms.dtos.ListOfAddStock;
import com.example.wms.dtos.ValidationDto;

public interface IncomingGoodsServices {
    ValidationDto createIncomingGoodsOrOrder(Integer quantity, String name, String orderOrIG);
//    void checkIncomingGoods(AddIGDto addIGDto);
    ListOfAddStock findIncomingGoods(String incomingGoodsId);
    ListOfAddStock getAllIncomingGoods(Integer pageNo, Integer pageSize);
    void completeIncomingGoods(AddIGDto addIGDto);
}
