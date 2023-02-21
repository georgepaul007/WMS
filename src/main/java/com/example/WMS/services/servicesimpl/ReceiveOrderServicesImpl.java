package com.example.WMS.services.servicesimpl;

import com.example.WMS.dtos.AddStockDescriptionDto;
import com.example.WMS.dtos.AddStockDto;
import com.example.WMS.dtos.ValidationDto;
import com.example.WMS.entity.Order;
import com.example.WMS.entity.ProductDetails;
import com.example.WMS.entity.ReceiveOrder;
import com.example.WMS.handlers.OrderHandler;
import com.example.WMS.handlers.ProductDetailsHandler;
import com.example.WMS.handlers.ReceiveOrdersHandler;
import com.example.WMS.services.ReceiveOrderServices;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ReceiveOrderServicesImpl implements ReceiveOrderServices {
    public ValidationDto createReceiveOrder(AddStockDto addStockDto) {
        String uniqueID = UUID.randomUUID().toString();
        ReceiveOrder receiveOrder = ReceiveOrder.builder()
                .receiveOrderId(uniqueID)
                .createdDate(new Date().getTime())
                .merchantId(addStockDto.getMerchantId())
                .productId(addStockDto.getProductId())
                .quantity(addStockDto.getQuantity())
                .build();
        ReceiveOrdersHandler receiveOrderHandler = new ReceiveOrdersHandler();
        ProductDetails productDetails = ProductDetailsHandler.getProductDetails();
        ProductDetailsHandler productReader = new ProductDetailsHandler();
        receiveOrderHandler.write(receiveOrder);
        productDetails.setQuantity(productDetails.getQuantity() + receiveOrder.getQuantity());
        productReader.write(productDetails);
        return ValidationDto.builder().isValid(true).reason(uniqueID).build();
    }
    public AddStockDescriptionDto findReceiveOrder(String receiveOrderId) {
        ReceiveOrdersHandler receiveOrdersHandler = new ReceiveOrdersHandler();
        return receiveOrdersHandler.read(receiveOrderId);
    }

    public List<AddStockDescriptionDto> getAllReceiveOrder(String pageNo, String pageSize) {
        ReceiveOrdersHandler receiveOrdersHandler = new ReceiveOrdersHandler();
        return receiveOrdersHandler.readPage(pageNo, pageSize);
    }


}
