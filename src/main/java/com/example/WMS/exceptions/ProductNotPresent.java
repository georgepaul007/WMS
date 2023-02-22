package com.example.WMS.exceptions;


public class ProductNotPresent extends NullPointerException{
    public ProductNotPresent() {
        super("Product is not present in data");
    }
}
