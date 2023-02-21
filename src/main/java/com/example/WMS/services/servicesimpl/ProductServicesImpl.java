package com.example.WMS.services.servicesimpl;

import com.example.WMS.entity.ProductDetails;
import com.example.WMS.handlers.ProductDetailsHandler;
import com.example.WMS.services.ProductServices;
import org.springframework.stereotype.Service;

@Service
public class ProductServicesImpl implements ProductServices {
    public ProductDetails getProductDetails() {
//        ProductDetailsHandler productDetailsHandler = n
        return ProductDetailsHandler.getProductDetails();

    }
}
