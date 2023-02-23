package com.example.wms.controller;


import com.example.wms.dtos.ListOfOrderDescription;
import com.example.wms.dtos.ValidationDto;
import com.example.wms.services.OrderServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/outbound")
public class OutboundController {
    @Autowired
    private OrderServices orderServices;

    @PostMapping("/createOrder")
    public ResponseEntity<ValidationDto> makeOrder(@RequestParam Integer quantity, @RequestParam String name) {
            ValidationDto valid = orderServices.createOrder(quantity, name);
            return new ResponseEntity<>(valid, HttpStatus.CREATED);

    }

    @GetMapping("/getOrder")
    public ResponseEntity<ListOfOrderDescription> getOrder(@RequestParam String orderId) {
        return new ResponseEntity<>(orderServices.findOrder(orderId), HttpStatus.OK);
    }

    @GetMapping("/getAllOrder")
    public ResponseEntity<ListOfOrderDescription> getAllOrder(@RequestParam Integer pageNo, @RequestParam Integer pageSize) {
        return new ResponseEntity<>(orderServices.getAllOrder(pageNo, pageSize), HttpStatus.OK);
    }
}
