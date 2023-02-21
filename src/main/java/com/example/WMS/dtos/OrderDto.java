package com.example.WMS.dtos;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
public class OrderDto {
    Integer quantity;
    String productId;
    String merchantId;
}
