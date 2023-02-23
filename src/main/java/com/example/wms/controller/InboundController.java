package com.example.wms.controller;

import com.example.wms.dtos.ListOfAddStock;
import com.example.wms.dtos.ValidationDto;
import com.example.wms.services.IncomingGoodsServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inbound")
@Slf4j
public class InboundController {
    @Autowired
    private IncomingGoodsServices incomingGoodsServices;

    @PostMapping("/createIncomingGoods")
    public ResponseEntity<ValidationDto> createIncomingGoods(@RequestParam Integer quantity, @RequestParam String name) {
        log.info("Quantity: {} Product Name: {}", quantity, name);
        return new ResponseEntity<>(incomingGoodsServices.createIncomingGoods(quantity, name), HttpStatus.CREATED);

    }

    @GetMapping("/getIncomingGoods")
    public ResponseEntity<ListOfAddStock> getIncomingGoods(@RequestParam String incomingGoodsId) {
        log.info("The received incoming goods ID: {}", incomingGoodsId);
        return new ResponseEntity<>(incomingGoodsServices.findIncomingGoods(incomingGoodsId), HttpStatus.OK);
    }

    @GetMapping("/getAllIncomingGoods")
    public ResponseEntity<ListOfAddStock> getAllIncomingGoods(@RequestParam Integer pageNo, @RequestParam Integer pageSize) {
        log.info("Incoming goods of page {} with page size {} requested!", pageNo, pageSize);
        return new ResponseEntity<>(incomingGoodsServices.getAllIncomingGoods(pageNo, pageSize), HttpStatus.OK);
    }
}
