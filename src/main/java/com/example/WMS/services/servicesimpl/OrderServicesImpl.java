package com.example.WMS.services.servicesimpl;

import com.example.WMS.dtos.ListOfOrderDescription;
import com.example.WMS.dtos.OrderDescriptionDto;
import com.example.WMS.dtos.OrderDto;
import com.example.WMS.dtos.ValidationDto;
import com.example.WMS.entity.Order;
import com.example.WMS.entity.ProductDetails;
import com.example.WMS.exceptions.OrderNotFound;
import com.example.WMS.exceptions.ProductNotPresent;
import com.example.WMS.handlers.OrderHandler;
import com.example.WMS.handlers.ProductDetailsHandler;
import com.example.WMS.services.OrderServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class OrderServicesImpl implements OrderServices {
    @Autowired
    OrderHandler orderHandler;
    @Autowired
    ProductDetailsHandler productDetailsHandler;
    public ValidationDto createOrder(OrderDto orderDto) {
        ProductDetails productDetails = productDetailsHandler.getProductDetails();
        log.info("Product details received from file are: {}", productDetails);

        try {
            if(productDetails == null) {
                throw new ProductNotPresent();
            }
            log.info("Product details received from file are: {}", productDetails);
        } catch (ProductNotPresent e) {
            log.error("Product is not present! {}", e);
            return ValidationDto.builder()
                    .isValid(false)
                    .reason("product not found")
                    .build();
        } catch (Exception e) {
            log.error("Some error has occurred: {}", e);
            return ValidationDto.builder()
                    .isValid(false)
                    .reason("Some error occurred")
                    .build();
        }
        if(productDetails.getQuantity() < orderDto.getQuantity()) {
            return ValidationDto.builder().isValid(false).reason("Not enough Quantity").build();
        }
        String uniqueID = UUID.randomUUID().toString();
        Order order = Order.builder()
                .orderId(uniqueID)
                .createdDate(new Date().getTime())
                .merchantId(orderDto.getMerchantId())
                .productId(orderDto.getProductId())
                .quantity(orderDto.getQuantity())
                .build();
        orderHandler.write(order);
        productDetails.setQuantity(productDetails.getQuantity() - order.getQuantity());
        ProductDetailsHandler productReader = new ProductDetailsHandler();
        productReader.write(productDetails);
        return ValidationDto.builder().isValid(true).reason(uniqueID).build();
    }
    public ListOfOrderDescription findOrder(String orderId) {
        OrderDescriptionDto orderDescriptionDto = orderHandler.read(orderId);
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
                .orderDescriptionDtos(new ArrayList<>(Arrays.asList(orderDescriptionDto)))
                .build();

    }
    public ListOfOrderDescription getAllOrder(String pageNo, String pageSize) {
        List<OrderDescriptionDto> orderDescriptionDtos = orderHandler.readPage(pageNo, pageSize);
        try {
            if(orderDescriptionDtos == null) {
                throw new OrderNotFound();
            }
        } catch (OrderNotFound e) {
            log.error("Page not found in database!");
            return ListOfOrderDescription.builder()
                    .isPresent(false)
                    .build();
        }
        return ListOfOrderDescription.builder()
                .isPresent(true)
                .orderDescriptionDtos(new ArrayList<>(orderDescriptionDtos))
                .build();
    }
}