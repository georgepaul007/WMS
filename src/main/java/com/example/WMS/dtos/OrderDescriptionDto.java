package com.example.WMS.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class OrderDescriptionDto {
    String orderId;
    String createdDate;
    String productId;
    String merchantId;
    Integer quantity;
}
