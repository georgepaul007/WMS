package com.example.wms.services.servicesimpl;

import com.example.wms.dtos.AddProductDto;
import com.example.wms.dtos.CurrentProductDetails;
import com.example.wms.dtos.ProductDetailsDto;
import com.example.wms.dtos.ValidationDto;
import com.example.wms.entity.ProductDetails;
import com.example.wms.repo.ProductDetailsRepository;
import com.example.wms.services.ProductServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ProductServicesImpl implements ProductServices {

    @Autowired
    private ProductDetailsRepository productDetailsRepository;
    public ProductDetailsDto getProductDetails(String name) {
        Optional<ProductDetails> productDetails = productDetailsRepository.findByProductName(name);
        if(productDetails.isPresent()) {
            return ProductDetailsDto.builder()
                    .isPresent(true)
                    .productDetails(CurrentProductDetails.builder()
                            .lastIncomingGoods(new java.util.Date(productDetails.get().getLastIncomingGoods()).toString())
                            .lastOrder(new java.util.Date(productDetails.get().getLastOrder()).toString())
                            .merchantId(productDetails.get().getMerchantId())
                            .price(productDetails.get().getPrice())
                            .productName(productDetails.get().getProductName())
                            .quantity(productDetails.get().getQuantity())
                            .build())
                    .build();
        }

        return ProductDetailsDto.builder().isPresent(false).build();
    }
    public ValidationDto addProduct(AddProductDto addProductDto) {
        ProductDetails productDetails = ProductDetails.builder()
                .merchantId(addProductDto.getMerchantId())
                .price(addProductDto.getPrice())
                .productName(addProductDto.getProductName())
                .quantity(addProductDto.getQuantity())
                .build();
        Optional<ProductDetails> productDetails1 = productDetailsRepository.findByProductName(productDetails.getProductName());
        if(productDetails1.isPresent()) {
            return ValidationDto.builder()
                    .isValid(false)
                    .reason("Product already exists")
                    .build();
        }
        productDetailsRepository.save(productDetails);
        return ValidationDto.builder()
                .isValid(true)
                .reason("Product added!")
                .build();
    }
}
