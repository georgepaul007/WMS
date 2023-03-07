package com.example.wms.kafkalisteners;

import com.example.wms.dtos.AddIGDto;
import com.example.wms.entity.ProductDetails;
import com.example.wms.handlers.IncomingGoodsHandler;
import com.example.wms.handlers.ProductDetailsHandler;
import com.example.wms.services.IncomingGoodsServices;
import com.example.wms.services.OrderServices;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class KafkaListenerProductDetails {
    @Value("${topic.name.edit-product-details}")
    private String topicName;
    @Autowired
    private IncomingGoodsHandler incomingGoodsHandler;

    @Autowired
    private IncomingGoodsServices incomingGoodsServices;
    @Autowired
    private ProductDetailsHandler productDetailsHandler;

    @Autowired
    private OrderServices orderServices;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${topic.name.product}")
    private String productTopic;

    @KafkaListener(topics = "${topic.name.edit-product-details}", groupId = "group_id")
    public void consume(ConsumerRecord<String, String> payload) throws JsonProcessingException, IOException {
        log.info("topic is: {}", topicName);
        String incomingGoodsOrOrder = payload.key();
        ObjectMapper mapper = new ObjectMapper();
        AddIGDto addIGDto = mapper.readValue(payload.value(), AddIGDto.class);
        if (incomingGoodsOrOrder.equals("incoming")) {
            kafkaTemplate.send(productTopic, "incoming", addIGDto);
            return;
        }
        kafkaTemplate.send(productTopic,"order", addIGDto);
    }
    @KafkaListener(topics = "${topic.name.product}", groupId = "group_id")
    public void consume1(ConsumerRecord<String, String> payload) throws JsonProcessingException, IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        ObjectMapper mapper = new ObjectMapper();
        AddIGDto productDetails = mapper.readValue(payload.value(), AddIGDto.class);
        String incomingGoodsOrOrder = payload.key();
        ProductDetails productDetails1 = productDetailsHandler.getProductDetails(productDetails.getName());
        if(incomingGoodsOrOrder.equals("order")) {
            productDetails1.setQuantity(productDetails1.getQuantity() - productDetails.getQuantity());
        } else {
            productDetails1.setQuantity(productDetails1.getQuantity() + productDetails.getQuantity());

        }
        productDetailsHandler.editProduct(productDetails1, productDetails.getUUID());

    }
}
