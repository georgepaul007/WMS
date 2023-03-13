package com.example.wms.services;


import com.example.wms.dtos.ChangeQuantityDto;
import com.example.wms.dtos.ListOfAddStock;
import com.example.wms.dtos.ListOfOrderItem;
import com.example.wms.dtos.ValidationDto;

public interface IncomingGoodsServices {
    ValidationDto createIncomingGoodsOrOrder(ListOfOrderItem listOfOrderItem, String orderOrIG);
//    void checkIncomingGoods(AddIGDto addIGDto);
    ListOfAddStock findIncomingGoods(String incomingGoodsId);
    ListOfAddStock getAllIncomingGoods(Integer pageNo, Integer pageSize);
    boolean completeIncomingGoods(ChangeQuantityDto changeQuantityDto);
    ValidationDto putawayItem(String productName, String incomingGoodsId);
}
