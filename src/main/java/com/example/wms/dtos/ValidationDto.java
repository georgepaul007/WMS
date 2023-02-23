package com.example.wms.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidationDto {
    Boolean isValid;
    String reason;
}
