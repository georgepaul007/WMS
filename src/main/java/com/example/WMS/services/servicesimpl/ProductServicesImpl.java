package com.example.WMS.services.servicesimpl;

import com.example.WMS.entity.ProductDetails;
import com.example.WMS.handlers.ProductDetailsHandler;
import com.example.WMS.services.ProductServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServicesImpl implements ProductServices {
    @Autowired
    ProductDetailsHandler productDetailsHandler;

    public ProductDetails getProductDetails() {
        return productDetailsHandler.getProductDetails();

    }
}
