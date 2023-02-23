package com.example.wms.services.servicesimpl;

import com.example.wms.dtos.ListOfOrderDescription;
import com.example.wms.dtos.OrderDescriptionDto;
import com.example.wms.dtos.ValidationDto;
import com.example.wms.entity.Order;
import com.example.wms.entity.ProductDetails;
import com.example.wms.exceptions.OrderNotFound;
import com.example.wms.exceptions.PageDoesNotContainValues;
import com.example.wms.exceptions.PageNeedsToBeGreaterThanZero;
import com.example.wms.handlers.OrderHandler;
import com.example.wms.handlers.ProductDetailsHandler;
import com.example.wms.services.OrderServices;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OrderServicesImpl implements OrderServices {

    @Autowired
    private OrderHandler orderHandler;

    @Autowired
    private ProductDetailsHandler productDetailsHandler;

    public ValidationDto createOrder(Integer quantity, String name) {
        ProductDetails productDetails = null;
        try {
            productDetails = productDetailsHandler.getProductDetails(name);
        } catch (IOException e) {
            log.error("Exception occurred while reading product details file: {}", e);
        }
        log.info("Product details received from file are: {}", productDetails);
         if(productDetails == null) {
             log.error("Product is not present!");
             return ValidationDto.builder()
                     .isValid(false)
                     .reason("product not found")
                     .build();
         }
         log.info("Product details received from file are: {}", productDetails);
        if(productDetails.getQuantity() < quantity) {
            return ValidationDto.builder().isValid(false).reason("Not enough Quantity").build();
        }
        String uniqueID = UUID.randomUUID().toString();
        Order order = Order.builder()
                .orderId(uniqueID)
                .createdDate(new Date().getTime())
                .newQuantity(productDetails.getQuantity() - quantity)
                .previousQuantity(productDetails.getQuantity())
                .merchantId(productDetails.getMerchantId())
                .productId(productDetails.getProductId())
                .quantity(quantity)
                .build();
        orderHandler.write(order);
        productDetails.setQuantity(productDetails.getQuantity() - order.getQuantity());
        try {
            productDetailsHandler.editProduct(productDetails);
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
        return ValidationDto.builder().isValid(true).reason(uniqueID).build();
    }
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
            log.error("Page does not contain any values! {}", e);
            return ListOfOrderDescription.builder()
                    .isPresent(false)
                    .reason("page has no entries")
                    .build();
        } catch (PageNeedsToBeGreaterThanZero e) {
            log.error("PageNo and pagesize needs to be greater than 0! {}", e);
            return ListOfOrderDescription.builder()
                    .isPresent(false)
                    .reason("Pageno or pagesize lesser than 1")
                    .build();
        } catch (Exception e) {
            log.error("error occurred while reading values! {}", e);
        }
        return ListOfOrderDescription.builder()
                .isPresent(true)
                .orderDescriptionDtos(orderDescriptionDtos)
                .build();
    }
}