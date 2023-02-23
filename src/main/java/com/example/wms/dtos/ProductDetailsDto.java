package com.example.wms.dtos;

import com.example.wms.entity.ProductDetails;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProductDetailsDto {
    Boolean isPresent;
    String reason;
    CurrentProductDetails productDetails;
}
