package com.example.wms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailsDto implements Serializable {
    Boolean isPresent;
    String reason;
    CurrentProductDetails productDetails;


}
