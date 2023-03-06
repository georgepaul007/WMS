package com.example.wms.services.servicesimpl;

import com.example.wms.dtos.AddIGDto;
import com.example.wms.dtos.ListOfOrderDescription;
import com.example.wms.dtos.OrderDescriptionDto;
import com.example.wms.entity.Order;
import com.example.wms.entity.ProductDetails;
import com.example.wms.exceptions.OrderNotFound;
import com.example.wms.exceptions.PageDoesNotContainValues;
import com.example.wms.exceptions.PageNeedsToBeGreaterThanZero;
import com.example.wms.handlers.OrderHandler;
import com.example.wms.handlers.ProductDetailsHandler;
import com.example.wms.services.OrderServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class OrderServicesImpl implements OrderServices {

    @Value("${topic.name.edit-product-details}")
    private String topicName;


    @Autowired
    private ProductDetailsHandler productDetailsHandler;
    @Autowired
    private OrderHandler orderHandler;

    public static AtomicReference<ProductDetails> productDetails = new AtomicReference<>();
    public ListOfOrderDescription findOrder(String orderId) {
        OrderDescriptionDto orderDescriptionDto = null;
        try {
            orderDescriptionDto = orderHandler.read(orderId);
        } catch (IOException e) {
            log.error("Error occurred while adding order details! {}", e);
        }
        try {
            if(orderDescriptionDto == null) {
                throw new OrderNotFound();
            }
        } catch(OrderNotFound e) {
            log.error("Order was not found!");
            return ListOfOrderDescription.builder()
                    .isPresent(false)
                    .build();
        }
        return ListOfOrderDescription.builder()
                .isPresent(true)
                .orderDescriptionDtos(Arrays.asList(orderDescriptionDto))
                .build();

    }
    public ListOfOrderDescription getAllOrder(Integer pageNo, Integer pageSize) {
        List<OrderDescriptionDto> orderDescriptionDtos = null;
        try {
            orderDescriptionDtos = orderHandler.readPage(pageNo, pageSize);
        } catch (PageDoesNotContainValues e) {
            log.error("Page does not contain any values!");
            return ListOfOrderDescription.builder()
                    .isPresent(false)
                    .reason("page has no entries")
                    .build();
        } catch (PageNeedsToBeGreaterThanZero e) {
            log.error("PageNo and pagesize needs to be greater than 0!");
            return ListOfOrderDescription.builder()
                    .isPresent(false)
                    .reason("Pageno or pagesize lesser than 1")
                    .build();
        } catch (Exception e) {
            log.error("error occurred while reading values!");
            return ListOfOrderDescription.builder()
                    .isPresent(false)
                    .reason("error occurred while reading file!")
                    .build();
        }
        return ListOfOrderDescription.builder()
                .isPresent(true)
                .orderDescriptionDtos(orderDescriptionDtos)
                .build();
    }
    public void completeOrder(AddIGDto addIGDto) {
        try {
            productDetails.set(productDetailsHandler.getProductDetails(addIGDto.getName()));
        } catch (IOException e) {
            log.error("Error while reading file...");
        }
        if (productDetails == null) {
            log.error("Product is not present!");
        }
        Order order = Order.builder()
                .createdDate(new Date().getTime())
                .orderId(addIGDto.getUUID())
                .merchantId(productDetails.get().getMerchantId())
                .productId(productDetails.get().getProductId())
                .newQuantity(productDetails.get().getQuantity() - addIGDto.getQuantity())
                .previousQuantity(productDetails.get().getQuantity())
                .quantity(addIGDto.getQuantity())
                .build();
        if(productDetails.get().getQuantity() < addIGDto.getQuantity()) {
            order.setStatus("Abandoned due to no stock");
            orderHandler.write(order);
            log.error("Not enough quantity while adding");
            return;
        }
        order.setStatus("Successfully ordered!");
        orderHandler.write(order);
    }
    public ListOfOrderDescription getByStatus(String status, Integer pageNo, Integer pageSize) {
        List<OrderDescriptionDto> orderDescriptionDtos = null;
        try {
            orderDescriptionDtos = orderHandler.readPageByStatus(status, pageNo, pageSize);
        } catch (PageDoesNotContainValues e) {
            log.error("Page does not contain any values!");
            return ListOfOrderDescription.builder()
                .isPresent(false)
                .reason("page has no entries")
                .build();
        } catch (PageNeedsToBeGreaterThanZero e) {
            log.error("PageNo and pagesize needs to be greater than 0!");
            return ListOfOrderDescription.builder()
                .isPresent(false)
                .reason("Pageno or pagesize lesser than 1")
                .build();
        } catch (Exception e) {
            log.error("error occurred while reading values!", e);
            e.printStackTrace();
            return ListOfOrderDescription.builder()
                .isPresent(false)
                .reason("error occurred while reading file!")
                .build();
        }
            return ListOfOrderDescription.builder()
                .isPresent(true)
                .orderDescriptionDtos(orderDescriptionDtos)
                .build();
    }

}