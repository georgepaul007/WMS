package com.example.wms.dtos;

import lombok.Data;

@Data
public class AddProductDto {
    Integer quantity;
    String productId;
    String productName;
    String merchantId;
    Double price;
}
