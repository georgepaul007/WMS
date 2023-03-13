package com.example.wms.dtos;
import com.example.wms.entity.IncomingGoods;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListOfAddStock {
    List<IncomingGoods> incomingGoodsList;
    Boolean isPresent;
    String reason;
}
