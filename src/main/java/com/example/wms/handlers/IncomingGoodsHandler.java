package com.example.wms.handlers;

import com.example.wms.constants.FilePaths;
import com.example.wms.dtos.AddStockDescriptionDto;
import com.example.wms.entity.IncomingGoods;
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

@Component
@Slf4j
public class IncomingGoodsHandler {
    public AddStockDescriptionDto read(String incomingGoodsId) throws IOException{
        try (Reader reader = Files.newBufferedReader(Paths.get(FilePaths.INCOMING_GOODS))) {
            CsvToBean<IncomingGoods> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(IncomingGoods.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            Iterator<IncomingGoods> csvIncomingGoodsIterator = csvToBean.iterator();
            while (csvIncomingGoodsIterator.hasNext()) {
                IncomingGoods incomingGoods = csvIncomingGoodsIterator.next();
                if(incomingGoods.getIncomingGoodsId().equals(incomingGoodsId)) {
                    return AddStockDescriptionDto.builder()
                            .createdDate(new java.util.Date(incomingGoods.getCreatedDate()).toString())
                            .incomingGoodsId(incomingGoods.getIncomingGoodsId())
                            .merchantId(incomingGoods.getMerchantId())
                            .productId(incomingGoods.getProductId())
                            .quantity(incomingGoods.getQuantity())
                            .newQuantity(incomingGoods.getNewQuantity())
                            .previousQuantity(incomingGoods.getPreviousQuantity())
                            .build();
                }
            }
        }
        return null;
    }
    public void write (IncomingGoods incomingGoods) {
        try (Writer writer = Files.newBufferedWriter(Paths.get(FilePaths.INCOMING_GOODS), StandardOpenOption.APPEND)) {
            StatefulBeanToCsv<IncomingGoods> sbc = new StatefulBeanToCsvBuilder(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build();
            sbc.write(Arrays.asList(incomingGoods));
        }
        catch (Exception e) {
            log.error("Some error occurred! {}", e);
        }
    }
    public List<AddStockDescriptionDto> readPage(Integer pageNo, Integer pageSize) throws PageNeedsToBeGreaterThanZero, PageDoesNotContainValues, IOException {
        List<AddStockDescriptionDto> addStockDescriptionDtos = new ArrayList<>();
        if(pageNo < 1 || pageSize < 1) {
            log.error("PageNo and pagesize needs to be greater than 0");
            throw new PageNeedsToBeGreaterThanZero();
        }
        try (Reader reader = Files.newBufferedReader(Paths.get(FilePaths.INCOMING_GOODS))) {
            CsvToBean<IncomingGoods> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(IncomingGoods.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            List<IncomingGoods> csvIncomingGodsDetails = csvToBean.parse();
            if(csvIncomingGodsDetails.size() <= (pageNo-1) * pageSize) {
                log.error("Page does not contain any values!");
                throw new PageDoesNotContainValues();
            }
            Integer firstPage = (pageNo - 1) * pageSize;
            for(int i = firstPage; i < firstPage + pageSize && i <= csvIncomingGodsDetails.size() - 1; i++) {
                addStockDescriptionDtos.add(AddStockDescriptionDto.builder()
                        .createdDate(new java.util.Date(csvIncomingGodsDetails.get(i).getCreatedDate()).toString())
                        .incomingGoodsId(csvIncomingGodsDetails.get(i).getIncomingGoodsId())
                        .merchantId(csvIncomingGodsDetails.get(i).getMerchantId())
                        .productId(csvIncomingGodsDetails.get(i).getProductId())
                        .quantity(csvIncomingGodsDetails.get(i).getQuantity())
                        .newQuantity(csvIncomingGodsDetails.get(i).getNewQuantity())
                        .previousQuantity(csvIncomingGodsDetails.get(i).getPreviousQuantity())
                        .build());
            }

        }
        return addStockDescriptionDtos;
    }
}
