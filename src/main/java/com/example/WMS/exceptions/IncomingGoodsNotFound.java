package com.example.WMS.exceptions;

public class IncomingGoodsNotFound extends Exception{
    public IncomingGoodsNotFound() {
        super("Incoming Goods was not found with given ID!");
    }
}
