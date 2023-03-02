package com.example.wms.kafkalisteners;

import com.example.wms.dtos.AddIGDto;
import com.example.wms.entity.IncomingGoods;
import com.example.wms.handlers.IncomingGoodsHandler;
import com.example.wms.handlers.ProductDetailsHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;

import java.io.IOException;
import java.util.Date;

import static com.example.wms.services.servicesimpl.IncomingGoodsServicesImpl.productDetails;
@Slf4j
public class KafkaListenerProductDetails {
    @Value("${topic.name.edit-product-details}")
    private String topicName;
    @Autowired
    private IncomingGoodsHandler incomingGoodsHandler;

    @Autowired
    private ProductDetailsHandler productDetailsHandler;
    @KafkaListener(topics = "${topic.name.edit-product-details}", groupId = "group_id")
    public void consume(ConsumerRecord<String, String> payload) throws JsonProcessingException {
        log.info("topic is: {}", topicName);
        String incomingGoodsOrOrder = payload.key();
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(incomingGoodsOrOrder);
        AddIGDto addIGDto = mapper.readValue(payload.value(), AddIGDto.class);
        System.out.println(addIGDto);
        String uniqueID = addIGDto.getUUID();
        if(incomingGoodsOrOrder.equals("incoming")) {
            try {
                productDetails.set(productDetailsHandler.getProductDetails(addIGDto.getName()));
            } catch (Exception e) {
                log.error("Error occurred while reading product details {}", e);
            }
            if (productDetails == null) {
                log.error("Product is not present!");
            }
            //log.info("Product details received from file are: {}", productDetails);
            productDetails.get().setQuantity(productDetails.get().getQuantity() + addIGDto.getQuantity());
            try {
                productDetailsHandler.editProduct(productDetails.get());
            } catch (CsvDataTypeMismatchException e) {

                log.error("CSV and data do not match! {}", e);
                e.printStackTrace();
            } catch (CsvRequiredFieldEmptyException e) {
                log.error("A required field was empty! {}", e);
                e.printStackTrace();
            } catch (IOException e) {
                log.error("Exception occurred while reading file! {}", e);
                e.printStackTrace();
            }
            IncomingGoods incomingGoods = IncomingGoods.builder()
                    .incomingGoodsId(uniqueID)
                    .createdDate(new Date().getTime())
                    .merchantId(productDetails.get().getMerchantId())
                    .productId(productDetails.get().getProductId())
                    .quantity(addIGDto.getQuantity())
                    .newQuantity(productDetails.get().getQuantity())
                    .previousQuantity(productDetails.get().getQuantity() - addIGDto.getQuantity())
                    .build();
            log.info("Product details received from file are: {}", productDetails);
            incomingGoodsHandler.write(incomingGoods);
            return;
        }

    }
}
