package com.example.WMS.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ReceiveOrder {
    String receiveOrderId;
    Long createdDate;
    Integer quantity;
    String productId;
    String merchantId;
}
