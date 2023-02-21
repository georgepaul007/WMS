package com.example.WMS.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class ProductDetails {
    Integer quantity;
    String productId;
    String productName;
    String merchantId;
    Double price;
}
