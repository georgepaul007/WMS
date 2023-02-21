package com.example.WMS.controller;


import com.example.WMS.dtos.AddStockDescriptionDto;
import com.example.WMS.dtos.OrderDescriptionDto;
import com.example.WMS.dtos.OrderDto;
import com.example.WMS.dtos.ValidationDto;
import com.example.WMS.services.OrderServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/outbound")
public class OutboundController {
    @Autowired
    OrderServices orderServices;

    @PostMapping("/createOrder")
    public ResponseEntity<ValidationDto> makeOrder(@RequestBody OrderDto orderDto) {
            ValidationDto valid = orderServices.createOrder(orderDto);
            return new ResponseEntity<>(valid, HttpStatus.CREATED);

    }

    @GetMapping("/getOrder")
    public ResponseEntity<OrderDescriptionDto> getOrder(@RequestParam String orderId) {
        return new ResponseEntity<>(orderServices.findOrder(orderId), HttpStatus.OK);
    }
    @GetMapping("/getAllReceiveOrder")
    public ResponseEntity<List<OrderDescriptionDto>> getAllReceiveOrder(@RequestParam String pageNo, @RequestParam String pageSize) {
        return new ResponseEntity<>(orderServices.getAllOrder(pageNo, pageSize), HttpStatus.OK);
    }
}
