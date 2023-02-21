package com.example.WMS.handlers;

import com.example.WMS.dtos.AddStockDescriptionDto;
import com.example.WMS.dtos.OrderDescriptionDto;
import com.example.WMS.entity.ReceiveOrder;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
public class ReceiveOrdersHandler{
    public AddStockDescriptionDto read(String receiveOrderId) {
        try (CSVReader reader = new CSVReader(new FileReader("/Users/georgepaul/Downloads/WMS/src/main/java/com/example/WMS/csv/ReceiveOrder.csv"))) {
            String[] lineInArray;
            while ((lineInArray = reader.readNext()) != null) {
                if(lineInArray[0].equals(receiveOrderId)) {
                    return AddStockDescriptionDto.builder()
                            .orderId(receiveOrderId)
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
    public void write (ReceiveOrder receiveOrder) {
        try{
            String csv = "/Users/georgepaul/Downloads/WMS/src/main/java/com/example/WMS/csv/ReceiveOrder.csv";
            CSVWriter writer = new CSVWriter(new FileWriter(csv, true));

            String [] record = new String[] {receiveOrder.getReceiveOrderId(), receiveOrder.getCreatedDate().toString(), receiveOrder.getMerchantId(), receiveOrder.getProductId(), receiveOrder.getQuantity()+""};
            writer.writeNext(record);

            writer.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    public List<AddStockDescriptionDto> readPage(String pageNo, String pageSize) {
        try (CSVReader reader = new CSVReader(new FileReader("/Users/georgepaul/Downloads/WMS/src/main/java/com/example/WMS/csv/ReceiveOrder.csv"), ',', '\'', (Integer.parseInt(pageNo)-1) * Integer.parseInt(pageSize))) {
            String[] lineInArray;
            int page = Integer.parseInt(pageNo) - 1;
            List<AddStockDescriptionDto> addStockDescriptionDtos = new ArrayList<>();
            int i = 0;
            while ((lineInArray = reader.readNext()) != null && i < Integer.parseInt(pageSize)) {
                try {
                    lineInArray[0].replaceAll("\"", "");
                    System.out.println(lineInArray);
                    addStockDescriptionDtos.add(AddStockDescriptionDto.builder()
                            .orderId(lineInArray[0].replaceAll("\"", ""))
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
            return addStockDescriptionDtos;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
