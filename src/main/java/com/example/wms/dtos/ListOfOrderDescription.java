package com.example.wms.dtos;

import com.example.wms.entity.Orders;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
public class ListOfOrderDescription {
    List<Orders> ordersList;
    Boolean isPresent;
    String reason;
}
