package com.example.wms.services;

import com.example.wms.dtos.AddProductDto;
import com.example.wms.dtos.ProductDetailsDto;
import com.example.wms.dtos.ValidationDto;

public interface ProductServices {
    ProductDetailsDto getProductDetails(String name);
    ValidationDto addProduct(AddProductDto addProductDto);
}
