package com.example.WMS.services.servicesimpl;

import com.example.WMS.dtos.AddStockDescriptionDto;
import com.example.WMS.dtos.OrderDescriptionDto;
import com.example.WMS.dtos.OrderDto;
import com.example.WMS.dtos.ValidationDto;
import com.example.WMS.entity.Order;
import com.example.WMS.entity.ProductDetails;
import com.example.WMS.handlers.OrderHandler;
import com.example.WMS.handlers.ProductDetailsHandler;
import com.example.WMS.handlers.ReadWriteHandler;
import com.example.WMS.handlers.ReceiveOrdersHandler;
import com.example.WMS.services.OrderServices;
import org.springframework.stereotype.Service;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServicesImpl implements OrderServices {
    public ValidationDto createOrder(OrderDto orderDto) {

        ProductDetails productDetails = ProductDetailsHandler.getProductDetails();
//        Thread thread = new Thread(new ReadThread(productReader, "read"));
//        thread.start();
//        try{
//            thread.join();
//        }
//        catch (Exception e) {
//            System.out.println(e);
//        }
        System.out.println(productDetails.toString());
        if(productDetails.getQuantity() < orderDto.getQuantity()) {
            return ValidationDto.builder().isValid(false).reason("Not enough Quantity").build();
        }
        String uniqueID = UUID.randomUUID().toString();
        Order order = Order.builder()
                .orderId(uniqueID)
                .createdDate(new Date().getTime())
                .merchantId(orderDto.getMerchantId())
                .productId(orderDto.getProductId())
                .quantity(orderDto.getQuantity())
                .build();
        OrderHandler orderHandler = new OrderHandler();
        orderHandler.write(order);
        productDetails.setQuantity(productDetails.getQuantity() - order.getQuantity());
        ProductDetailsHandler productReader = new ProductDetailsHandler();
        productReader.write(productDetails);
        return ValidationDto.builder().isValid(true).reason(uniqueID).build();
//        try {
//            Writer writer = Files.newBufferedWriter(Paths.get("/Users/georgepaul/Downloads/WMS/src/main/java/com/example/WMS/csv/Order.csv"));
//        }
//        catch (Exception e) {
//            System.out.println("Error occurred while writing: " + e);
//        }
//        if()
    }
    public OrderDescriptionDto findOrder(String orderId) {
        OrderHandler orderHandler = new OrderHandler();
        return orderHandler.read(orderId);
    }
    public List<OrderDescriptionDto> getAllOrder(String pageNo, String pageSize) {
        OrderHandler ordersHandler = new OrderHandler();
        return ordersHandler.readPage(pageNo, pageSize);
    }
}
