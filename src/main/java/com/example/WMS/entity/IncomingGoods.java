package com.example.WMS.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class IncomingGoods {
    String incomingGoodsId;
    Long createdDate;
    Integer quantity;
    String productId;
    String merchantId;
}
