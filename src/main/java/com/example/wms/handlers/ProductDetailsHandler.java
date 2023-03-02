package com.example.wms.handlers;


import com.example.wms.constants.FilePaths;
import com.example.wms.dtos.ValidationDto;
import com.example.wms.entity.ProductDetails;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class ProductDetailsHandler {
    static AtomicReference<ProductDetails> details = new AtomicReference<>();

    public ProductDetails getProductDetails(String name) throws IOException {
        try (Reader reader = Files.newBufferedReader(Paths.get(FilePaths.PRODUCT_DETAILS))) {
            CsvToBean<ProductDetails> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(ProductDetails.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            Iterator<ProductDetails> csvProductIterator = csvToBean.iterator();
            while (csvProductIterator.hasNext()) {
                details.set(csvProductIterator.next());
                if (details.get().getProductName().equals(name)) {
                    return details.get();
                }
            }
            return null;

        }
    }

    public void editProduct(ProductDetails productDetails) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        Path path = Paths.get(FilePaths.PRODUCT_DETAILS);
        try (Reader reader = Files.newBufferedReader(path)) {
            CsvToBean<ProductDetails> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(ProductDetails.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            List<ProductDetails> csvProductList = csvToBean.parse();
            for(Iterator<ProductDetails> iterator = csvProductList.iterator(); iterator.hasNext(); ) {
                ProductDetails details = iterator.next();
                if (details.getProductName().equals(productDetails.getProductName())) {
                    if (details.getQuantity() > productDetails.getQuantity()) {
                        productDetails.setLastOrder(new Date().getTime());
                    } else {
                        productDetails.setLastIncomingGoods(new Date().getTime());
                    }
                    iterator.remove();
                }
            }
            csvProductList.add(productDetails);
            try (Writer writer = Files.newBufferedWriter(path)) {
                StatefulBeanToCsv<ProductDetails> sbc = new StatefulBeanToCsvBuilder(writer)
                        .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                        .build();
                sbc.write(csvProductList);
            }
        }
    }

    public ValidationDto addProduct(ProductDetails productDetails) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        try (Reader reader = Files.newBufferedReader(Paths.get(FilePaths.PRODUCT_DETAILS))) {
            CsvToBean<ProductDetails> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(ProductDetails.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            Iterator<ProductDetails> csvUserIterator = csvToBean.iterator();
//            csvUserIterator.forEachRemaining(productDetails1 -> {
//                if(productDetails1.getProductName().equals(productDetails.getProductName())) {
//
//                }
//            });
            while(csvUserIterator.hasNext()) {
                ProductDetails currentDetails = csvUserIterator.next();
                if(currentDetails.getProductName().equals(productDetails.getProductName())) {
                    return ValidationDto.builder()
                            .isValid(false)
                            .reason("Product Already Present")
                            .build();
                }
            }

            try (Writer writer = Files.newBufferedWriter(Paths.get(FilePaths.PRODUCT_DETAILS), StandardOpenOption.APPEND)) {
                StatefulBeanToCsv<ProductDetails> sbc = new StatefulBeanToCsvBuilder(writer)
                        .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                        .build();
                sbc.write(Arrays.asList(productDetails));
            }
            return ValidationDto.builder()
                    .isValid(true)
                    .reason("Product Added")
                    .build();
        }
    }
}
