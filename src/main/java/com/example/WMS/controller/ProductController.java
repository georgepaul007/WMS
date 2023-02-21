package com.example.WMS.controller;

import com.example.WMS.entity.ProductDetails;
import com.example.WMS.services.ProductServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Response;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    ProductServices productServices;

    @GetMapping("/getProductDetails")
    public ResponseEntity<ProductDetails> getProductDetails() {
        return new ResponseEntity<>(productServices.getProductDetails(), HttpStatus.OK);
    }
}
