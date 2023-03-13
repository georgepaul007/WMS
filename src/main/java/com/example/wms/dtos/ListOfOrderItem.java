package com.example.wms.dtos;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class ListOfOrderItem {
    List<SingleOrderItem> singleOrderItemList;
}
