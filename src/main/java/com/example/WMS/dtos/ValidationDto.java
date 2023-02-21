package com.example.WMS.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidationDto {
    Boolean isValid;
    String reason;
}
