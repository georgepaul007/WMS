package com.example.wms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeQuantityDto {
    String UUID;
    Integer quantity;
    String name;

    String incomingOrOrder;
}
