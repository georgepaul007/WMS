package com.example.WMS.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Builder
@Data
public class Order implements Serializable {

    String orderId;
    Long createdDate;
    String productId;
    String merchantId;
    Integer quantity;

}
