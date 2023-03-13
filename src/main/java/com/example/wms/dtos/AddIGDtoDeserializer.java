package com.example.wms.dtos;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

public class AddIGDtoDeserializer implements Deserializer<ChangeQuantityDto> {
    @Override
    public ChangeQuantityDto deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(data, ChangeQuantityDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing object", e);
        }
    }
}
