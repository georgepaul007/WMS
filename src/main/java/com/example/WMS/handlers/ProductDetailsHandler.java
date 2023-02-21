package com.example.WMS.handlers;


import com.example.WMS.entity.Order;
import com.example.WMS.entity.ProductDetails;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import java.awt.print.Book;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProductDetailsHandler{
    static ProductDetails productDetails;
    public void read() {
        Path pathToFile = Paths.get("/Users/georgepaul/Downloads/WMS/src/main/java/com/example/WMS/csv/ProductDetails.csv");
        try (BufferedReader br = Files.newBufferedReader(pathToFile,
                StandardCharsets.US_ASCII)) {
            String line = br.readLine();
            while (line != null) {
                String[] attributes = line.split(",");

                productDetails = ProductDetails.builder()
                        .price(Double.parseDouble(attributes[0]))
                        .quantity(Integer.parseInt(attributes[1]))
                        .productId(attributes[2])
                        .productName(attributes[3])
                        .merchantId(attributes[4])
                        .build();
                line = br.readLine();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
//        try (CSVReader reader = new CSVReader(new FileReader("/Users/georgepaul/Downloads/WMS/src/main/java/com/example/WMS/csv/Order.csv"))) {
//            String[] lineInArray;
//            while ((lineInArray = reader.readNext()) != null) {
//                productDetails = ProductDetails.builder()
//                        .quantity(Integer.parseInt(lineInArray[0]))
//                        .productId(lineInArray[1])
//                        .productName(lineInArray[2])
//                        .build();
//            }
//        }

    }
    public static ProductDetails getProductDetails() {
        Path pathToFile = Paths.get("/Users/georgepaul/Downloads/WMS/src/main/java/com/example/WMS/csv/ProductDetails.csv");
//        try (BufferedReader br = Files.newBufferedReader(pathToFile,
//                StandardCharsets.US_ASCII)) {
//            String line = br.readLine();
//            while (line != null) {
//                String[] attributes = line.split(",");
//
//                productDetails = ProductDetails.builder()
//                        .price(Double.parseDouble(attributes[0]))
//                        .quantity(Integer.parseInt(attributes[1]))
//                        .productId(attributes[2])
//                        .productName(attributes[3])
//                        .merchantId(attributes[4])
//                        .build();
//                line = br.readLine();
//                return productDetails;
//
//            }
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            FileReader filereader = new FileReader("/Users/georgepaul/Downloads/WMS/src/main/java/com/example/WMS/csv/ProductDetails.csv");
            CSVReader csvReader = new CSVReader(filereader);
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
//        try{
//            String path = "/Users/georgepaul/Downloads/WMS/src/main/java/com/example/WMS/csv/ProductDetails.csv";
//            Writer writer = Files.newBufferedWriter(Paths.get(path));
//            ColumnPositionMappingStrategy mappingStrategy=
//                    new ColumnPositionMappingStrategy();
//            mappingStrategy.setType(ProductDetails.class);
//            String[] columns = new String[] { "price", "quantity", "productId", "productName", "merchantId" };
//            mappingStrategy.setColumnMapping(columns);
//            StatefulBeanToCsvBuilder<ProductDetails> builder= new StatefulBeanToCsvBuilder(writer);
//            StatefulBeanToCsv beanWriter = builder.withMappingStrategy(mappingStrategy).build();
//            synchronized (ProductDetailsHandler.class) {
//                beanWriter.write(productDetails1);
//            }
//
//            writer.close();
//        }
//        catch(Exception e) {
//            e.printStackTrace();
//        }

        try {
            String csv = "/Users/georgepaul/Downloads/WMS/src/main/java/com/example/WMS/csv/ProductDetails.csv";
            CSVWriter writer = new CSVWriter(new FileWriter(csv, false));

            String [] record = new String[] {productDetails1.getPrice().toString(), productDetails1.getQuantity().toString(), productDetails1.getProductId(), productDetails1.getProductName(), productDetails1.getMerchantId()};
            writer.writeNext(record);

            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//            String csv = "/Users/georgepaul/Downloads/WMS/src/main/java/com/example/WMS/csv/ProductDetails.csv";
//            CSVWriter writer = new CSVWriter(new FileWriter(csv, false));
//
//            String[] record = new String[]{productDetails.getPrice().toString(), productDetails.getQuantity().toString(), productDetails.getMerchantId(), productDetails.getProductName(), productDetails.getProductId()};
//            writer.writeNext(record);
//
//            writer.close();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
