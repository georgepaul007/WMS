package com.example.WMS.handlers;

import com.example.WMS.constants.FilePaths;
import com.example.WMS.dtos.AddStockDescriptionDto;
import com.example.WMS.entity.IncomingGoods;
import com.example.WMS.exceptions.IncomingGoodsNotFound;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class IncomingGoodsHandler {
    public AddStockDescriptionDto read(String receiveOrderId) {
        try (CSVReader reader = new CSVReader(new FileReader(FilePaths.INCOMING_GOODS))) {
            String[] currentIncomingGoods;
            while ((currentIncomingGoods = reader.readNext()) != null) {
                if(currentIncomingGoods[0].equals(receiveOrderId)) {
                    log.info("Read line is: {}", Arrays.toString(currentIncomingGoods));
                    return AddStockDescriptionDto.builder()
                            .orderId(receiveOrderId)
                            .createdDate(new java.util.Date(Long.parseLong(currentIncomingGoods[1])).toString())
                            .merchantId(currentIncomingGoods[3])
                            .productId(currentIncomingGoods[2])
                            .quantity(Integer.parseInt(currentIncomingGoods[4]))
                            .build();
                }
            }
            throw new IncomingGoodsNotFound();
        }
        catch (IncomingGoodsNotFound e) {
            log.info("Incoming goods not found with given ID: {}", e);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public void write (IncomingGoods incomingGoods) {
        try(CSVWriter writeIncomingGoods = new CSVWriter(new FileWriter(FilePaths.INCOMING_GOODS, true))){
            String [] incomingGoodsDetailsToBeAdded = new String[] {incomingGoods.getIncomingGoodsId(), incomingGoods.getCreatedDate().toString(), incomingGoods.getMerchantId(), incomingGoods.getProductId(), incomingGoods.getQuantity()+""};
            writeIncomingGoods.writeNext(incomingGoodsDetailsToBeAdded);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    public List<AddStockDescriptionDto> readPage(String pageNo, String pageSize) {
        CSVReader readIncomingGoods = null;
        try  {
            readIncomingGoods = new CSVReader(new FileReader(FilePaths.INCOMING_GOODS), ',', '\"', (Integer.parseInt(pageNo)-1) * Integer.parseInt(pageSize));
            String[] currentIncomingGoods;
            List<AddStockDescriptionDto> addStockDescriptionDtos = new ArrayList<>();
            int i = 0;
            while ((currentIncomingGoods = readIncomingGoods.readNext()) != null && i < Integer.parseInt(pageSize)) {
                log.info("line read is: {}", Arrays.toString(currentIncomingGoods));
                addStockDescriptionDtos.add(AddStockDescriptionDto.builder()
                        .orderId(currentIncomingGoods[0])
                        .createdDate(new java.util.Date(Long.parseLong(currentIncomingGoods[1])).toString())
                        .merchantId(currentIncomingGoods[3])
                        .productId(currentIncomingGoods[2])
                        .quantity(Integer.parseInt(currentIncomingGoods[4]))
                        .build());

                i++;
            }
            if(addStockDescriptionDtos.get(0) == null) {
                throw new IncomingGoodsNotFound();
            }
            return addStockDescriptionDtos;
        } catch (IncomingGoodsNotFound e) {
            log.info("Incoming goods not found with given ID: {}", e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(readIncomingGoods != null) {
                try {
                    readIncomingGoods.close();
                }
                catch (IOException e) {
                    log.error("Error occurred while closing file: ", e);
                }
            }
        }
        return null;
    }
}
