package com.example.wms.services.servicesimpl;

import com.example.wms.dtos.AddIGDto;
import com.example.wms.dtos.AddStockDescriptionDto;
import com.example.wms.dtos.ListOfAddStock;
import com.example.wms.dtos.ValidationDto;
import com.example.wms.entity.IncomingGoods;
import com.example.wms.entity.ProductDetails;
import com.example.wms.exceptions.PageDoesNotContainValues;
import com.example.wms.exceptions.PageNeedsToBeGreaterThanZero;
import com.example.wms.handlers.IncomingGoodsHandler;
import com.example.wms.handlers.ProductDetailsHandler;
import com.example.wms.services.IncomingGoodsServices;
import com.example.wms.services.OrderServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;


@Service
@Slf4j
public class IncomingGoodsServicesImpl implements IncomingGoodsServices {
    @Value("${topic.name.edit-product-details}")
    private String topicName;
    @Autowired
    private IncomingGoodsHandler incomingGoodsHandler;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ProductDetailsHandler productDetailsHandler;


    @Autowired
    private OrderServices orderServices;
    public static AtomicReference<ProductDetails> productDetails = new AtomicReference<>();

//    @KafkaListener(topics = "${topic.name.edit-product-details}", groupId = "group_id")
//    public void consume(ConsumerRecord<String, String> payload) throws JsonProcessingException {
//        log.info("topic is: {}", topicName);
//        String incomingGoodsOrOrder = payload.key();
//        ObjectMapper mapper = new ObjectMapper();
//        AddIGDto addIGDto = mapper.readValue(payload.value(), AddIGDto.class);
//        if (incomingGoodsOrOrder.equals("incoming")) {
//            completeIncomingGoods(addIGDto);
//            return;
//        }
//        orderServices.completeOrder(addIGDto);
//    }



    public ValidationDto createIncomingGoodsOrOrder(Integer quantity, String name, String orderOrIG) {
        String uniqueID = UUID.randomUUID().toString();
        try {
            productDetails.set(productDetailsHandler.getProductDetails(name));
        } catch (IOException e) {
            log.error("Error while reading file...");
            return ValidationDto.builder()
                    .isValid(false)
                    .reason("Error while reading products file")
                    .build();
        }
        if (productDetails == null) {
            log.error("Product is not present!");
            return ValidationDto.builder()
                    .isValid(false)
                    .reason("product not found")
                    .build();
        }
        if(orderOrIG.equals("incoming")) {
            kafkaTemplate.send(topicName, "incoming", AddIGDto.builder().UUID(uniqueID).name(name).quantity(quantity).build());
        } else {
            kafkaTemplate.send(topicName, "order", AddIGDto.builder().UUID(uniqueID).name(name).quantity(quantity).build());
        }
        return ValidationDto.builder().isValid(true).reason(uniqueID).build();
    }

    public ListOfAddStock findIncomingGoods(String incomingGoodsId) {
        AddStockDescriptionDto addStockDescriptionDtos = null;
        try {
            addStockDescriptionDtos = incomingGoodsHandler.read(incomingGoodsId);
        } catch(IOException e) {
            e.printStackTrace();
            log.error("Error while reading incoming goods!");
        }
        if (addStockDescriptionDtos == null) {
            log.error("Page not found in database!");
            return ListOfAddStock.builder()
                    .isPresent(false)
                    .build();
            }

        return ListOfAddStock.builder()
                .isPresent(true)
                .addStockDescriptionDtos(Arrays.asList(addStockDescriptionDtos))
                .build();
    }

    public ListOfAddStock getAllIncomingGoods(Integer pageNo, Integer pageSize) {
        List<AddStockDescriptionDto> addStockDescriptionDtos = null;
        try {
            addStockDescriptionDtos = incomingGoodsHandler.readPage(pageNo, pageSize);
        } catch (PageDoesNotContainValues e) {
            e.printStackTrace();
            log.error("Page does not contain any values!");
            return ListOfAddStock.builder()
                    .isPresent(false)
                    .reason("page has no entries")
                    .build();
        } catch (PageNeedsToBeGreaterThanZero e) {
            e.printStackTrace();
            log.error("PageNo and pagesize needs to be greater than 0!");
            return ListOfAddStock.builder()
                    .isPresent(false)
                    .reason("Pageno or pagesize lesser than 1")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("error occurred while reading values!");
            return ListOfAddStock.builder()
                    .isPresent(false)
                    .reason("error occurred while reading file")
                    .build();
        }
        return ListOfAddStock.builder()
                .isPresent(true)
                .addStockDescriptionDtos(addStockDescriptionDtos)
                .build();
    }
    public void completeIncomingGoods(AddIGDto addIGDto) {
            try {
                productDetails.set(productDetailsHandler.getProductDetails(addIGDto.getName()));
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Error occurred while reading product details");
                return;
            }
            if (productDetails == null) {
                log.error("Product is not present!");
                return;
            }

                IncomingGoods incomingGoods = IncomingGoods.builder()
                        .incomingGoodsId(addIGDto.getUUID())
                        .createdDate(new Date().getTime())
                        .merchantId(productDetails.get().getMerchantId())
                        .productId(productDetails.get().getProductId())
                        .quantity(addIGDto.getQuantity())
                        .newQuantity(productDetails.get().getQuantity())
                        .previousQuantity(productDetails.get().getQuantity())
                        .build();
                log.info("Product details received from file are: {}", productDetails);
                incomingGoodsHandler.write(incomingGoods);



    }
}