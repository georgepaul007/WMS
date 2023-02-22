package com.example.WMS.handlers;


import com.example.WMS.constants.FilePaths;
import com.example.WMS.entity.Order;
import com.example.WMS.entity.ProductDetails;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.springframework.stereotype.Component;

import java.awt.print.Book;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ProductDetailsHandler{
    public ProductDetails getProductDetails() {
        try (CSVReader csvReader = new CSVReader(new FileReader(FilePaths.PRODUCT_DETAILS))){
            String[] productDetailsString = csvReader.readNext();
            return ProductDetails.builder()
                    .price(Double.parseDouble(productDetailsString[0]))
                    .quantity(Integer.parseInt(productDetailsString[1]))
                    .merchantId(productDetailsString[2])
                    .productName(productDetailsString[3])
                    .productId(productDetailsString[4])
                    .build();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public void write(ProductDetails productDetails1) {
        try(CSVWriter writer = new CSVWriter(new FileWriter(FilePaths.PRODUCT_DETAILS, false))) {
            String [] productDetailsToBeWritted = new String[] {productDetails1.getPrice().toString(), productDetails1.getQuantity().toString(), productDetails1.getProductId(), productDetails1.getProductName(), productDetails1.getMerchantId()};
            writer.writeNext(productDetailsToBeWritted);
            }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
