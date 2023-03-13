package com.example.wms.controller;

import com.example.wms.dtos.ListOfAddStock;
import com.example.wms.dtos.ListOfOrderItem;
import com.example.wms.dtos.ValidationDto;
import com.example.wms.services.IncomingGoodsServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<ValidationDto> createIncomingGoods(@RequestBody ListOfOrderItem listOfOrderItem) {
        log.info("incoming goods {}", listOfOrderItem.toString());
        return new ResponseEntity<>(incomingGoodsServices.createIncomingGoodsOrOrder(listOfOrderItem, "incoming"), HttpStatus.OK);
    }

    @PostMapping("/getIncomingGoods")
    public ResponseEntity<ListOfAddStock> getIncomingGoods(@RequestParam String incomingGoodsId) {
        log.info("The received incoming goods ID: {}", incomingGoodsId);
        return new ResponseEntity<>(incomingGoodsServices.findIncomingGoods(incomingGoodsId), HttpStatus.OK);
    }

    @GetMapping("/getAllIncomingGoods")
    public ResponseEntity<ListOfAddStock> getAllIncomingGoods(@RequestParam Integer pageNo, @RequestParam Integer pageSize) {
        log.info("Incoming goods of page {} with page size {} requested!", pageNo, pageSize);
        return new ResponseEntity<>(incomingGoodsServices.getAllIncomingGoods(pageNo, pageSize), HttpStatus.OK);
    }

    @PostMapping("putawayItem")
    public ResponseEntity<ValidationDto> putawayItem(@RequestParam String productName, @RequestParam String incomingGoodsId) {
        return new ResponseEntity<>(incomingGoodsServices.putawayItem(productName,incomingGoodsId), HttpStatus.OK);
    }
}
