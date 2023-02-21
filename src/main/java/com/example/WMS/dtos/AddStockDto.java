package com.example.WMS.dtos;

import lombok.Builder;
import lombok.Data;

@Data
public class AddStockDto {
    String merchantId;
    String productId;
    Integer quantity;

}
