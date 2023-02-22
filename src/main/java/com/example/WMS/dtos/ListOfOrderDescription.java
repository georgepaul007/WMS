package com.example.WMS.dtos;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class ListOfOrderDescription {
    List<OrderDescriptionDto> orderDescriptionDtos;
    Boolean isPresent;
}
