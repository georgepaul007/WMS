package com.example.wms.controller;


import com.example.wms.dtos.ListOfOrderDescription;
import com.example.wms.dtos.ListOfOrderItem;
import com.example.wms.dtos.ValidationDto;
import com.example.wms.services.IncomingGoodsServices;
import com.example.wms.services.OrderServices;
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
@RequestMapping("/outbound")
@Slf4j
public class OutboundController {
    @Autowired
    private OrderServices orderServices;

    @Autowired
    private IncomingGoodsServices incomingGoodsServices;

    @PostMapping("/createOrder")
    public ResponseEntity<ValidationDto> makeOrder(@RequestBody ListOfOrderItem listOfOrderItem) {
            ValidationDto valid = incomingGoodsServices.createIncomingGoodsOrOrder(listOfOrderItem, "order");
            return new ResponseEntity<>(valid, HttpStatus.CREATED);
    }

    @GetMapping("/getOrder")
    public ResponseEntity<ListOfOrderDescription> getOrder(@RequestParam String orderId) {
        ListOfOrderDescription listOfOrderDescription = orderServices.findOrder(orderId);
        log.info("List of order description received! {}", listOfOrderDescription);
        return new ResponseEntity<>(listOfOrderDescription, HttpStatus.OK);
    }

    @GetMapping("/getAllOrder")
    public ResponseEntity<ListOfOrderDescription> getAllOrder(@RequestParam Integer pageNo, @RequestParam Integer pageSize) {
        return new ResponseEntity<>(orderServices.getAllOrder(pageNo, pageSize), HttpStatus.OK);
    }
    @GetMapping("/getByStatus")
    public ResponseEntity<ListOfOrderDescription> getByStatus(@RequestParam String status, @RequestParam Integer pageNo, @RequestParam Integer pageSize) {
        return new ResponseEntity<>(orderServices.getByStatus(status, pageNo, pageSize), HttpStatus.OK);
    }

    @PostMapping("/pickItem")
    public ResponseEntity<ValidationDto> pickItem(@RequestParam String productName, @RequestParam String orderId) {
        return new ResponseEntity<>(orderServices.pickItem(productName, orderId), HttpStatus.OK);
    }
}
