package com.example.WMS.handlers;

import com.example.WMS.dtos.AddStockDescriptionDto;
import com.example.WMS.dtos.OrderDescriptionDto;
import com.example.WMS.entity.Order;
import com.example.WMS.entity.ProductDetails;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderHandler {
//    String fileName;
//    OrderHandler(String fileName) {
//        this.fileName = fileName;
//    }
//    @Override
//    public void run() {
//        try {
//            FileReader filereader = new FileReader(fileName);
//            CSVReader csvReader = new CSVReader(filereader);
//        }
//        catch(Exception e) {
//            System.out.println("Error occurred while reading: " + e);
//        }
//    }
    public OrderDescriptionDto read(String orderId1) {
        try (CSVReader reader = new CSVReader(new FileReader("/Users/georgepaul/Downloads/WMS/src/main/java/com/example/WMS/csv/Order.csv"))) {
            String[] lineInArray;
            while ((lineInArray = reader.readNext()) != null) {
                if(lineInArray[0].equals(orderId1)) {
                    return OrderDescriptionDto.builder()
                            .orderId(orderId1)
                            .createdDate(new java.util.Date(Long.parseLong(lineInArray[1])).toString())
                            .merchantId(lineInArray[3])
                            .productId(lineInArray[2])
                            .quantity(Integer.parseInt(lineInArray[4]))
                            .build();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;


    }
    public void write (Order order) {
        try{
//            String path = "/Users/georgepaul/Downloads/WMS/src/main/java/com/example/WMS/csv/Order.csv";
//
//            Writer writer = Files.newBufferedWriter(Paths.get(path));
//            ColumnPositionMappingStrategy mappingStrategy= new ColumnPositionMappingStrategy();
//            mappingStrategy.setType(Order.class);
//            String[] columns = new String[] { "createdDate", "productId", "merchantId", "quantity" };
//            mappingStrategy.setColumnMapping(columns);
//            StatefulBeanToCsvBuilder<Order> builder= new StatefulBeanToCsvBuilder(writer);
//            StatefulBeanToCsv beanWriter = builder.withMappingStrategy(mappingStrategy).build();
//            synchronized (ProductDetailsHandler.class) {
//                beanWriter.(order);
//            }
//
//            writer.close();
            String csv = "/Users/georgepaul/Downloads/WMS/src/main/java/com/example/WMS/csv/Order.csv";
            CSVWriter writer = new CSVWriter(new FileWriter(csv, true));

            String [] record = new String[] {order.getOrderId(), order.getCreatedDate().toString(), order.getMerchantId(), order.getProductId(), order.getQuantity()+""};
            writer.writeNext(record);

            writer.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    public List<OrderDescriptionDto> readPage(String pageNo, String pageSize) {
        try (CSVReader reader = new CSVReader(new FileReader("/Users/georgepaul/Downloads/WMS/src/main/java/com/example/WMS/csv/Order.csv"), ',', '\'', (Integer.parseInt(pageNo)-1) * Integer.parseInt(pageSize))) {
            String[] lineInArray;
            int page = Integer.parseInt(pageNo) - 1;
            List<OrderDescriptionDto> orderDescriptionDtos = new ArrayList<>();
            int i = 0;
            while ((lineInArray = reader.readNext()) != null && i < Integer.parseInt(pageSize)) {
                try {
                    orderDescriptionDtos.add(OrderDescriptionDto.builder()
                            .orderId(lineInArray[0])
                            .createdDate(new java.util.Date(Long.parseLong(lineInArray[1].replaceAll("\"", ""))).toString())
                            .merchantId(lineInArray[3].replaceAll("\"", ""))
                            .productId(lineInArray[2].replaceAll("\"", ""))
                            .quantity(Integer.parseInt(lineInArray[4].replaceAll("\"", "")))
                            .build());
                }catch(NumberFormatException ex) {
                    ex.printStackTrace();
                }
                i++;
            }
            return orderDescriptionDtos;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
