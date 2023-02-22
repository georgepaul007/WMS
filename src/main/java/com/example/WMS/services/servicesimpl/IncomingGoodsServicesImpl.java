package com.example.WMS.services.servicesimpl;

import com.example.WMS.dtos.*;
import com.example.WMS.entity.IncomingGoods;
import com.example.WMS.entity.ProductDetails;
import com.example.WMS.exceptions.OrderNotFound;
import com.example.WMS.exceptions.ProductNotPresent;
import com.example.WMS.handlers.ProductDetailsHandler;
import com.example.WMS.handlers.IncomingGoodsHandler;
import com.example.WMS.services.IncomingGoodsServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class IncomingGoodsServicesImpl implements IncomingGoodsServices {

    @Autowired
    private IncomingGoodsHandler incomingGoodsHandler;

    @Autowired
    private ProductDetailsHandler productDetailsHandler;

    public ValidationDto createIncomingGoods(AddStockDto addStockDto) {
        String uniqueID = UUID.randomUUID().toString();
        IncomingGoods incomingGoods = IncomingGoods.builder()
                .incomingGoodsId(uniqueID)
                .createdDate(new Date().getTime())
                .merchantId(addStockDto.getMerchantId())
                .productId(addStockDto.getProductId())
                .quantity(addStockDto.getQuantity())
                .build();
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
        incomingGoodsHandler.write(incomingGoods);
        productDetails.setQuantity(productDetails.getQuantity() + incomingGoods.getQuantity());
        productDetailsHandler.write(productDetails);
        return ValidationDto.builder().isValid(true).reason(uniqueID).build();
    }

    public ListOfAddStock findReceiveOrder(String incomingGoodsId) {
        AddStockDescriptionDto addStockDescriptionDtos = incomingGoodsHandler.read(incomingGoodsId);
        try {
            if(addStockDescriptionDtos == null) {
                throw new OrderNotFound();
            }
        } catch (OrderNotFound e) {
            log.error("Page not found in database!");
            return ListOfAddStock.builder()
                    .isPresent(false)
                    .build();
        }
        return ListOfAddStock.builder()
                .isPresent(true)
                .addStockDescriptionDtos(new ArrayList<>(Arrays.asList(addStockDescriptionDtos)))
                .build();
    }

    public ListOfAddStock getAllReceiveOrder(String pageNo, String pageSize) {
        List<AddStockDescriptionDto> addStockDescriptionDtos = incomingGoodsHandler.readPage(pageNo, pageSize);
        try {
            if(addStockDescriptionDtos == null) {
                throw new OrderNotFound();
            }
        } catch (OrderNotFound e) {
            log.error("Page not found in database!");
            return ListOfAddStock.builder()
                    .isPresent(false)
                    .build();
        }
        return ListOfAddStock.builder()
                .isPresent(true)
                .addStockDescriptionDtos(new ArrayList<>(addStockDescriptionDtos))
                .build();
    }
}
