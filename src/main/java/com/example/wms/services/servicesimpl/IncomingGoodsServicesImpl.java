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
    private KafkaTemplate<String, AddIGDto> kafkaTemplate;

    @Autowired
    private ProductDetailsHandler productDetailsHandler;


    @Autowired
    private OrderServices orderServices;
    public static AtomicReference<ProductDetails> productDetails = new AtomicReference<>();

    @KafkaListener(topics = "${topic.name.edit-product-details}", groupId = "group_id")
    public void consume(ConsumerRecord<String, String> payload) throws JsonProcessingException {
        log.info("topic is: {}", topicName);
        String incomingGoodsOrOrder = payload.key();
        ObjectMapper mapper = new ObjectMapper();
        AddIGDto addIGDto = mapper.readValue(payload.value(), AddIGDto.class);
        String uniqueID = addIGDto.getUUID();
        if(incomingGoodsOrOrder.equals("incoming")) {
            completeIncomingGoods(addIGDto);
            return;
        }
        orderServices.completeOrder(addIGDto);

    }



    public ValidationDto createIncomingGoods(Integer quantity, String name) {
        String uniqueID = UUID.randomUUID().toString();

        try {
            productDetails.set(productDetailsHandler.getProductDetails(name));
        } catch (IOException e) {
            log.error("Error while reading file... {}", e);
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
        kafkaTemplate.send(topicName ,"incoming", AddIGDto.builder().UUID(uniqueID).name(name).quantity(quantity).build());
        return ValidationDto.builder().isValid(true).reason(uniqueID).build();
    }

    public ListOfAddStock findIncomingGoods(String incomingGoodsId) {
        AddStockDescriptionDto addStockDescriptionDtos = null;
        try {
            addStockDescriptionDtos = incomingGoodsHandler.read(incomingGoodsId);
        } catch(IOException e) {
            log.error("Error while reading incoming goods! {}", e);
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
            log.error("Page does not contain any values! {}", e);
            return ListOfAddStock.builder()
                    .isPresent(false)
                    .reason("page has no entries")
                    .build();
        } catch (PageNeedsToBeGreaterThanZero e) {
            log.error("PageNo and pagesize needs to be greater than 0! {}", e);
            return ListOfAddStock.builder()
                    .isPresent(false)
                    .reason("Pageno or pagesize lesser than 1")
                    .build();
        } catch (Exception e) {
            log.error("error occurred while reading values! {}", e);
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
            if(productDetails == null) {
                log.error("Product is not present!");
                return;
            }
            productDetails.get().setQuantity(productDetails.get().getQuantity() + addIGDto.getQuantity());
            try {
                productDetailsHandler.editProduct(productDetails.get());
            } catch (CsvDataTypeMismatchException e) {

                log.error("CSV and data do not match!");
                e.printStackTrace();
                return;
            } catch (CsvRequiredFieldEmptyException e) {
                log.error("A required field was empty!");
                e.printStackTrace();
                return;
            } catch (IOException e) {
                log.error("Exception occurred while reading file!");
                e.printStackTrace();
                return;
            }

        IncomingGoods incomingGoods = IncomingGoods.builder()
                .incomingGoodsId(addIGDto.getUUID())
                .createdDate(new Date().getTime())
                .merchantId(productDetails.get().getMerchantId())
                .productId(productDetails.get().getProductId())
                .quantity(addIGDto.getQuantity())
                .newQuantity(productDetails.get().getQuantity())
                .previousQuantity(productDetails.get().getQuantity() - addIGDto.getQuantity())
                .build();
        log.info("Product details received from file are: {}", productDetails);
        incomingGoodsHandler.write(incomingGoods);
    }
}