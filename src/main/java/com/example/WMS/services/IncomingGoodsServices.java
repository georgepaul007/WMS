package com.example.WMS.services;

import com.example.WMS.dtos.*;
import java.util.List;
public interface IncomingGoodsServices {
    ValidationDto createIncomingGoods(AddStockDto addStockDto);
    ListOfAddStock findReceiveOrder(String incomingGoodsId);
    ListOfAddStock getAllReceiveOrder(String pageNo, String pageSize);
}
