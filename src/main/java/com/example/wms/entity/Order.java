package com.example.wms.entity;

import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @CsvBindByPosition(position = 0)
    String orderId;

    @CsvBindByPosition(position = 4)
    Long createdDate;

    @CsvBindByPosition(position = 1)
    String productId;

    @CsvBindByPosition(position = 2)
    String merchantId;

    @CsvBindByPosition(position = 3)
    Integer quantity;

    @CsvBindByPosition(position = 5)
    Integer previousQuantity;

    @CsvBindByPosition(position = 6)
    Integer newQuantity;

    @CsvBindByPosition(position = 7)
    String status;

}
