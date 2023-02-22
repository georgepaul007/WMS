package com.example.WMS.controller;


import com.example.WMS.dtos.*;
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
    public ResponseEntity<ListOfOrderDescription> getOrder(@RequestParam String orderId) {
        return new ResponseEntity<>(orderServices.findOrder(orderId), HttpStatus.OK);
    }

    @GetMapping("/getAllOrder")
    public ResponseEntity<ListOfOrderDescription> getAllOrder(@RequestParam String pageNo, @RequestParam String pageSize) {
        return new ResponseEntity<>(orderServices.getAllOrder(pageNo, pageSize), HttpStatus.OK);
    }
}
