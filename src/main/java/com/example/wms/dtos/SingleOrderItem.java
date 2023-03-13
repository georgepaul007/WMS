package com.example.wms.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class SingleOrderItem {
    String productName;
    Integer quantity;
}
