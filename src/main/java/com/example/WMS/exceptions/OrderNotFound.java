package com.example.WMS.exceptions;

public class OrderNotFound extends Exception {
    public OrderNotFound() {
        super("Order was not found!");
    }
}
