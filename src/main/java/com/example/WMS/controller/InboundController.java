package com.example.WMS.controller;

import com.example.WMS.dtos.*;
import com.example.WMS.services.OrderServices;
import com.example.WMS.services.ReceiveOrderServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/inbound")
public class InboundController {
    @Autowired
    ReceiveOrderServices receiveOrderServices;

    @PostMapping("/createReceiveOrder")
    public ResponseEntity<ValidationDto> makeReceiveOrder(@RequestBody AddStockDto addStockDto) {
        ValidationDto valid = receiveOrderServices.createReceiveOrder(addStockDto);
        return new ResponseEntity<>(valid, HttpStatus.CREATED);

    }

    @GetMapping("/getReceiveOrder")
    public ResponseEntity<AddStockDescriptionDto> getReceiveOrder(@RequestParam String receiveOrderId) {
        return new ResponseEntity<>(receiveOrderServices.findReceiveOrder(receiveOrderId), HttpStatus.OK);
    }

    @GetMapping("/getAllReceiveOrder")
    public ResponseEntity<List<AddStockDescriptionDto>> getAllReceiveOrder(@RequestParam String pageNo, @RequestParam String pageSize) {
        return new ResponseEntity<>(receiveOrderServices.getAllReceiveOrder(pageNo, pageSize), HttpStatus.OK);
    }
}
