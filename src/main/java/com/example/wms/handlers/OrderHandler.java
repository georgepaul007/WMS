package com.example.wms.handlers;

import com.example.wms.constants.FilePaths;
import com.example.wms.dtos.OrderDescriptionDto;
import com.example.wms.entity.Order;
import com.example.wms.exceptions.PageDoesNotContainValues;
import com.example.wms.exceptions.PageNeedsToBeGreaterThanZero;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OrderHandler {

    public OrderDescriptionDto read(String orderId1) throws IOException{
        try (Reader reader = Files.newBufferedReader(Paths.get(FilePaths.ORDER))) {
            CsvToBean<Order> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(Order.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            Iterator<Order> csvUserIterator = csvToBean.iterator();
            while (csvUserIterator.hasNext()) {
                Order orderDetails = csvUserIterator.next();
                if(orderDetails.getOrderId().equals(orderId1)) {
                    return OrderDescriptionDto.builder()
                            .createdDate(new java.util.Date(orderDetails.getCreatedDate()).toString())
                            .orderId(orderDetails.getOrderId())
                            .merchantId(orderDetails.getMerchantId())
                            .productId(orderDetails.getProductId())
                            .quantity(orderDetails.getQuantity())
                            .newQuantity(orderDetails.getNewQuantity())
                            .previousQuantity(orderDetails.getPreviousQuantity())
                            .build();
                }
            }
        }
        return null;


    }
    public void write (Order order) {
        try (Writer writer = Files.newBufferedWriter(Paths.get(FilePaths.ORDER), StandardOpenOption.APPEND)) {
            StatefulBeanToCsv<Order> sbc = new StatefulBeanToCsvBuilder(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build();
            sbc.write(Arrays.asList(order));
        }
        catch (Exception e) {
            log.error("Some error occurred!");
        }
    }
    public List<OrderDescriptionDto> readPageByStatus(String status, Integer pageNo, Integer pageSize) throws IOException, PageDoesNotContainValues, PageNeedsToBeGreaterThanZero {
        List<OrderDescriptionDto> orderDescriptionDtos = new ArrayList<>();
        if(pageNo < 1 || pageSize < 1) {
            log.error("PageNo and pagesize needs to be greater than 0");
            throw new PageNeedsToBeGreaterThanZero();
        }
        try (Reader reader = Files.newBufferedReader(Paths.get(FilePaths.ORDER))) {
            CsvToBean<Order> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(Order.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            List<Order> csvOrderDetails = csvToBean.parse();
            orderDescriptionDtos = csvOrderDetails.stream().filter(order -> order.getStatus().equals(status)).skip((pageNo - 1) * pageSize).limit(pageSize).map(order -> OrderDescriptionDto.builder()
                    .createdDate(new java.util.Date(order.getCreatedDate()).toString())
                    .orderId(order.getOrderId())
                    .merchantId(order.getMerchantId())
                    .productId(order.getProductId())
                    .quantity(order.getQuantity())
                    .newQuantity(order.getNewQuantity())
                    .previousQuantity(order.getPreviousQuantity())
                    .build()).collect(Collectors.toList());
        }
        return orderDescriptionDtos;
    }
    public List<OrderDescriptionDto> readPage(Integer pageNo, Integer pageSize) throws IOException, PageDoesNotContainValues, PageNeedsToBeGreaterThanZero{
        List<OrderDescriptionDto> orderDescriptionDtos = new ArrayList<>();
        if(pageNo < 1 || pageSize < 1) {
            log.error("PageNo and pagesize needs to be greater than 0");
            throw new PageNeedsToBeGreaterThanZero();
        }
        try (Reader reader = Files.newBufferedReader(Paths.get(FilePaths.ORDER))) {
            CsvToBean<Order> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(Order.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            List<Order> csvOrderDetails = csvToBean.parse();
            if(csvOrderDetails.size() <= (pageNo-1) * pageSize) {
                log.error("Page does not contain any values!");
                throw new PageDoesNotContainValues();
            }
            Integer firstPage = (pageNo - 1) * pageSize;
            for(int i = firstPage; i < firstPage + pageSize && i <= csvOrderDetails.size() - 1; i++) {
                orderDescriptionDtos.add(OrderDescriptionDto.builder()
                            .createdDate(new java.util.Date(csvOrderDetails.get(i).getCreatedDate()).toString())
                            .orderId(csvOrderDetails.get(i).getOrderId())
                            .merchantId(csvOrderDetails.get(i).getMerchantId())
                            .productId(csvOrderDetails.get(i).getProductId())
                            .quantity(csvOrderDetails.get(i).getQuantity())
                            .newQuantity(csvOrderDetails.get(i).getNewQuantity())
                            .previousQuantity(csvOrderDetails.get(i).getPreviousQuantity())
                            .build());
            }

        }
        return orderDescriptionDtos;
    }
}
