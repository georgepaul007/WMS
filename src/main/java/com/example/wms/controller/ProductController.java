package com.example.wms.controller;

import com.example.wms.dtos.AddProductDto;
import com.example.wms.dtos.ProductDetailsDto;
import com.example.wms.dtos.ValidationDto;
import com.example.wms.services.ProductServices;
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
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductServices productServices;

    @GetMapping("/getProductDetails")
    public ResponseEntity<ProductDetailsDto> getProductDetails(@RequestParam String name) {
        return new ResponseEntity<>(productServices.getProductDetails(name), HttpStatus.OK);
    }
    @PostMapping("/addProduct")
    public ResponseEntity<ValidationDto> getProductDetails(@RequestBody AddProductDto addProductDto) {
        return new ResponseEntity<>(productServices.addProduct(addProductDto), HttpStatus.OK);
    }
}
