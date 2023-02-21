package com.example.WMS.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddStockDescriptionDto {
    String orderId;
    String createdDate;
    String productId;
    String merchantId;
    Integer quantity;
}
