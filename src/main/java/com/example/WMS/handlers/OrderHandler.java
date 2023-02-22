package com.example.WMS.handlers;

import com.example.WMS.constants.FilePaths;
import com.example.WMS.dtos.AddStockDescriptionDto;
import com.example.WMS.dtos.OrderDescriptionDto;
import com.example.WMS.entity.Order;
import com.example.WMS.entity.ProductDetails;
import com.example.WMS.exceptions.OrderNotFound;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class OrderHandler {

    public OrderDescriptionDto read(String orderId1) {
        try (CSVReader orderReader = new CSVReader(new FileReader(FilePaths.ORDER))) {
            String[] currentOrderDescription;
            while ((currentOrderDescription = orderReader.readNext()) != null) {
                if(currentOrderDescription[0].equals(orderId1)) {
                    return OrderDescriptionDto.builder()
                            .orderId(orderId1)
                            .createdDate(new java.util.Date(Long.parseLong(currentOrderDescription[1])).toString())
                            .merchantId(currentOrderDescription[3])
                            .productId(currentOrderDescription[2])
                            .quantity(Integer.parseInt(currentOrderDescription[4]))
                            .build();
                }
            }
            throw new OrderNotFound();
        } catch (OrderNotFound e) {
            log.info("Order with given id not found: {}", e);
        } catch (IOException e) {
            log.info("Error occurred while reading from file: {}", e);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;


    }
    public void write (Order order) {
        try(CSVWriter orderWriter = new CSVWriter(new FileWriter(FilePaths.ORDER, true))){
            String [] orderToBeWritten = new String[] {order.getOrderId(), order.getCreatedDate().toString(), order.getMerchantId(), order.getProductId(), order.getQuantity()+""};
            log.info("Adding order details: {}", Arrays.toString(orderToBeWritten));
            orderWriter.writeNext(orderToBeWritten);
            }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    public List<OrderDescriptionDto> readPage(String pageNo, String pageSize) {
        try (CSVReader orderReader = new CSVReader(new FileReader(FilePaths.ORDER), ',', '\"', (Integer.parseInt(pageNo)-1) * Integer.parseInt(pageSize))) {
            String[] currentOrderDescription;
            List<OrderDescriptionDto> orderDescriptionDtos = new ArrayList<>();
            int i = 0;
            while ((currentOrderDescription = orderReader.readNext()) != null && i < Integer.parseInt(pageSize)) {
                try {
                    log.info("current order page: {}", Arrays.toString(currentOrderDescription));
                    orderDescriptionDtos.add(OrderDescriptionDto.builder()
                            .orderId(currentOrderDescription[0])
                            .createdDate(new java.util.Date(Long.parseLong(currentOrderDescription[1])).toString())
                            .merchantId(currentOrderDescription[3])
                            .productId(currentOrderDescription[2])
                            .quantity(Integer.parseInt(currentOrderDescription[4]))
                            .build());
                }catch(NumberFormatException ex) {
                    ex.printStackTrace();
                }
                i++;
            }
            if(orderDescriptionDtos.get(0) == null) {
                throw new OrderNotFound();
            }
            return orderDescriptionDtos;
        }
        catch (OrderNotFound e) {
            log.info("Order with given id not found: {}", e);
        } catch (IOException e) {
            log.info("Error occurred while reading from file: {}", e);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
