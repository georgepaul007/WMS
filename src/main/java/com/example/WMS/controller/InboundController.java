package com.example.WMS.controller;

import com.example.WMS.dtos.*;
import com.example.WMS.services.IncomingGoodsServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/inbound")
@Slf4j
public class InboundController {
    @Autowired
    IncomingGoodsServices incomingGoodsServices;

    @PostMapping("/createIncomingGoods")
    public ResponseEntity<ValidationDto> createIncomingGoods(@RequestBody AddStockDto addStockDto) {
        log.info("This is the incoming goods DTO: {}", addStockDto);
        ValidationDto valid = incomingGoodsServices.createIncomingGoods(addStockDto);
        return new ResponseEntity<>(valid, HttpStatus.CREATED);

    }

    @GetMapping("/getIncomingGoods")
    public ResponseEntity<ListOfAddStock> getIncomingGoods(@RequestParam String incomingGoodsId) {
        log.info("The received incoming goods ID: {}", incomingGoodsId);
        return new ResponseEntity<>(incomingGoodsServices.findReceiveOrder(incomingGoodsId), HttpStatus.OK);
    }

    @GetMapping("/getAllIncomingGoods")
    public ResponseEntity<ListOfAddStock> getAllIncomingGoods(@RequestParam String pageNo, @RequestParam String pageSize) {
        log.info("Incoming goods of page {} with page size {} requested!", pageNo, pageSize);
        return new ResponseEntity<>(incomingGoodsServices.getAllReceiveOrder(pageNo, pageSize), HttpStatus.OK);
    }
}
