package com.example.WMS.services;

import com.example.WMS.dtos.*;
import java.util.List;
public interface ReceiveOrderServices {
    ValidationDto createReceiveOrder(AddStockDto addStockDto);
    AddStockDescriptionDto findReceiveOrder(String receiveOrderId);
    List<AddStockDescriptionDto> getAllReceiveOrder(String pageNo, String pageSize);
}
