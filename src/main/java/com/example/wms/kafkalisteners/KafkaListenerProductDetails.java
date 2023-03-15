package com.example.wms.kafkalisteners;

import com.example.wms.dtos.ChangeQuantityDto;
import com.example.wms.entity.ProductDetails;
import com.example.wms.repo.ProductDetailsRepository;
import com.example.wms.services.IncomingGoodsServices;
import com.example.wms.services.OrderServices;
import com.example.wms.services.ProductServices;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class KafkaListenerProductDetails {
    @Value("${topic.name.edit-product-details}")
    private String topicName;

    @Autowired
    private IncomingGoodsServices incomingGoodsServices;

    @Autowired
    private OrderServices orderServices;
    @Autowired
    private ProductServices productServices;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${topic.name.product}")
    private String productTopic;
    @Autowired
    private ProductDetailsRepository productDetailsRepository;

    @KafkaListener(topics = "${topic.name.product}", groupId = "group_id")
    public void consume1(ConsumerRecord<String, String> payload) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        ChangeQuantityDto changeQuantityDto = mapper.readValue(payload.value(), ChangeQuantityDto.class);
        String productName = payload.key();
        Optional<ProductDetails> productDetails1 = productDetailsRepository.findByProductName(productName);
        if(!productDetails1.isPresent()) {
            log.error("Product Not Present");
            return;
        }
        if(changeQuantityDto.isOrder()) {
            orderServices.pickItem(changeQuantityDto.getName(),changeQuantityDto.getUUID() ,changeQuantityDto.getQuantity());
        } else {
            incomingGoodsServices.putawayItem(changeQuantityDto.getName(), changeQuantityDto.getUUID(), changeQuantityDto.getQuantity());
        }
    }
}
