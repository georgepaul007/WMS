//package com.example.WMS.thread;
//
//import com.example.WMS.entity.Order;
//import com.example.WMS.entity.ProductDetails;
//import com.example.WMS.handlers.ProductDetailsHandler;
//import com.example.WMS.handlers.ReadWriteHandler;
//
//public class ReadThread extends Thread {
//    ProductDetailsHandler readWriteHandler;
//    String method;
//    Order order;
//    public ReadThread(ProductDetailsHandler readWriteHandler, String method, Order order){
//        this.readWriteHandler = readWriteHandler;
//        this.method = method;
//        this.order = order;
//    }
//    @Override
//    public void run() {
//        if(method.equals("read")) {
//            readWriteHandler.read();
//        }
//        else {
//            readWriteHandler.write(order);
//        }
//    }
//}
