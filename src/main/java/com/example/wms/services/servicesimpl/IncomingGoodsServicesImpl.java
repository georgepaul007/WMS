package com.example.wms.services.servicesimpl;

import com.example.wms.dtos.AddStockDescriptionDto;
import com.example.wms.dtos.ListOfAddStock;
import com.example.wms.dtos.ValidationDto;
import com.example.wms.entity.IncomingGoods;
import com.example.wms.entity.ProductDetails;
import com.example.wms.exceptions.PageDoesNotContainValues;
import com.example.wms.exceptions.PageNeedsToBeGreaterThanZero;
import com.example.wms.handlers.ProductDetailsHandler;
import com.example.wms.handlers.IncomingGoodsHandler;
import com.example.wms.services.IncomingGoodsServices;
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
public class IncomingGoodsServicesImpl implements IncomingGoodsServices {

    @Autowired
    private IncomingGoodsHandler incomingGoodsHandler;

    @Autowired
    private ProductDetailsHandler productDetailsHandler;

    public ValidationDto createIncomingGoods(Integer quantity, String name) {
        String uniqueID = UUID.randomUUID().toString();
        ProductDetails productDetails = null;
        try {
            productDetails = productDetailsHandler.getProductDetails(name);
        } catch (Exception e) {
            log.error("Error occurred while reading product details {}", e);
        }
        if(productDetails == null) {
            return ValidationDto.builder()
                    .reason("Product not found!")
                    .isValid(false)
                    .build();
        }
        IncomingGoods incomingGoods = IncomingGoods.builder()
                .incomingGoodsId(uniqueID)
                .createdDate(new Date().getTime())
                .merchantId(productDetails.getMerchantId())
                .productId(productDetails.getProductId())
                .quantity(quantity)
                .newQuantity(quantity + productDetails.getQuantity())
                .previousQuantity(productDetails.getQuantity())
                .build();
        log.info("Product details received from file are: {}", productDetails);
        incomingGoodsHandler.write(incomingGoods);
        productDetails.setQuantity(productDetails.getQuantity() + incomingGoods.getQuantity());
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
        }
        return ListOfAddStock.builder()
                .isPresent(true)
                .addStockDescriptionDtos(addStockDescriptionDtos)
                .build();
    }
}