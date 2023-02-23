package com.example.wms.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddStockDescriptionDto {
    String incomingGoodsId;
    String createdDate;
    String productId;
    String merchantId;
    Integer quantity;
    Integer previousQuantity;
    Integer newQuantity;
}
