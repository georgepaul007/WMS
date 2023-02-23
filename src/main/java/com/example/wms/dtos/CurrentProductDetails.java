package com.example.wms.dtos;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class CurrentProductDetails {
    Integer quantity;

    String productId;

    String productName;

    String merchantId;

    Double price;

    String lastOrder;

    String lastIncomingGoods;
}
