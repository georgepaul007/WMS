package com.example.wms.entity;

import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductDetails {
    @CsvBindByPosition(position = 2)
    Integer quantity;

    @CsvBindByPosition(position = 0)
    String productId;

    @CsvBindByPosition(position = 1)
    String productName;

    @CsvBindByPosition(position = 3)
    String merchantId;

    @CsvBindByPosition(position = 4)
    Double price;

    @CsvBindByPosition(position = 5)
    Long lastOrder;

    @CsvBindByPosition(position = 6)
    Long lastIncomingGoods;


}
