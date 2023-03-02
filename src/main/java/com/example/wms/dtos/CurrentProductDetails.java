package com.example.wms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrentProductDetails implements Serializable {
    Integer quantity;

    String productId;

    String productName;

    String merchantId;

    Double price;

    String lastOrder;

    String lastIncomingGoods;
}
