package com.example.wms.dtos;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

public class AddIGDtoSerializer implements Serializer<ChangeQuantityDto> {
    @Override
    public byte[] serialize(String topic, ChangeQuantityDto data) {
        if (data == null) {
            return null;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing object", e);
        }
    }
}
