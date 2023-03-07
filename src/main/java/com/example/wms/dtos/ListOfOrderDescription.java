package com.example.wms.dtos;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class ListOfOrderDescription {
    List<OrderDescriptionDto> orderDescriptionDtos;
    Boolean isPresent;
    String reason;
}
