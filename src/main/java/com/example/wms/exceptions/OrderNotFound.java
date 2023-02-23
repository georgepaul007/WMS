package com.example.wms.exceptions;

public class OrderNotFound extends Exception {
    public OrderNotFound() {
        super("Order was not found!");
    }
}
