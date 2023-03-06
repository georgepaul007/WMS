package com.example.wms.services.servicesimpl;

import com.example.wms.dtos.AddProductDto;
import com.example.wms.dtos.CurrentProductDetails;
import com.example.wms.dtos.ProductDetailsDto;
import com.example.wms.dtos.ValidationDto;
import com.example.wms.entity.ProductDetails;
import com.example.wms.handlers.ProductDetailsHandler;
import com.example.wms.services.ProductServices;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class ProductServicesImpl implements ProductServices {
    @Autowired
    private ProductDetailsHandler productDetailsHandler;
    public ProductDetailsDto getProductDetails(String name) {
        ProductDetails productDetails;
        try {
            productDetails = productDetailsHandler.getProductDetails(name);
        } catch (IOException e) {
            log.error("Error occurred while reading for product details");
            return ProductDetailsDto.builder()
                    .reason("Error occurred while reading file!")
                    .isPresent(false)
                    .build();
        }
        if(productDetails == null) {
            return ProductDetailsDto.builder()
                    .reason("Product not present with given name!")
                    .isPresent(false)
                    .build();
        }
        return ProductDetailsDto.builder()
                .isPresent(true)
                .productDetails(CurrentProductDetails.builder()
                        .lastIncomingGoods(new java.util.Date(productDetails.getLastIncomingGoods()).toString())
                        .lastOrder(new java.util.Date(productDetails.getLastOrder()).toString())
                        .merchantId(productDetails.getMerchantId())
                        .price(productDetails.getPrice())
                        .productId(productDetails.getProductId())
                        .productName(productDetails.getProductName())
                        .quantity(productDetails.getQuantity())
                        .build())
                .build();
    }
    public ValidationDto addProduct(AddProductDto addProductDto) {
        ProductDetails productDetails = ProductDetails.builder()
                .merchantId(addProductDto.getMerchantId())
                .productId(addProductDto.getProductId())
                .price(addProductDto.getPrice())
                .productName(addProductDto.getProductName())
                .quantity(addProductDto.getQuantity())
                .build();
        try {
            return productDetailsHandler.addProduct(productDetails);
        } catch (CsvDataTypeMismatchException e) {
            log.error("CSV and data do not match!");
            e.printStackTrace();
            return ValidationDto.builder()
                    .isValid(false)
                    .reason("CSV and data dont match! server error")
                    .build();
        } catch (CsvRequiredFieldEmptyException e) {
            log.error("A required field was empty!");
            e.printStackTrace();
            return ValidationDto.builder()
                    .isValid(false)
                    .reason("Required field missing, server error")
                    .build();
        } catch (IOException e) {
            log.error("Exception occurred while reading file!");
            e.printStackTrace();
            return ValidationDto.builder()
                    .isValid(false)
                    .reason("Error occurred while reading file")
                    .build();
        }
    }
}
