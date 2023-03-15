package com.example.wms.services.servicesimpl;

import com.example.wms.dtos.ChangeQuantityDto;
import com.example.wms.dtos.ListOfOrderDescription;
import com.example.wms.dtos.ListOfOrderItem;
import com.example.wms.dtos.SingleOrderItem;
import com.example.wms.dtos.ValidationDto;
import com.example.wms.entity.IncomingGoods;
import com.example.wms.entity.Orders;
import com.example.wms.entity.ProductDetails;
import com.example.wms.entity.ProductIncoming;
import com.example.wms.entity.ProductOrders;
import com.example.wms.repo.OrderRepository;
import com.example.wms.repo.ProductDetailsRepository;
import com.example.wms.repo.ProductOrdersRepository;
import com.example.wms.services.OrderServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServicesImpl implements OrderServices {
    @Autowired
    private ProductDetailsRepository productDetailsRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductOrdersRepository productOrdersRepository;

    public static AtomicReference<ProductDetails> productDetails = new AtomicReference<>();
    public ValidationDto createOrder(ListOfOrderItem listOfOrderItem) {
        String uniqueID = UUID.randomUUID().toString();
        for (SingleOrderItem singleOrderItem: listOfOrderItem.getSingleOrderItemList()) {
            if (singleOrderItem.getQuantity() < 1) {
                log.error("Quantity was not positive!");
                return ValidationDto.builder()
                        .isValid(false)
                        .reason("Quantity must be positive!")
                        .build();
            }
            productDetails.set(productDetailsRepository.findByProductName(singleOrderItem.getProductName()).get());
            if (productDetails.get() == null) {
                log.error("product not present!");
                return ValidationDto.builder()
                        .isValid(false)
                        .reason("Product not present")
                        .build();
            }
            if(singleOrderItem.getQuantity() > (productDetails.get().getQuantity() - productOrdersRepository.findTotalQuantityOrdered(singleOrderItem.getProductName()))) {
                log.error("Not enough quantity, with product and product orders");
                return ValidationDto.builder()
                        .isValid(false)
                        .reason("Not enough Quantity")
                        .build();
            }
            Orders orders = Orders.builder()
                    .createdDate(new Date().getTime())
                    .orderId(uniqueID)
                    .build();
            orders.setStatus("OPEN");
            ProductOrders productOrders = new ProductOrders();
            productOrders.setQuantity(singleOrderItem.getQuantity());
            productOrders.setOrder(orders);
            productOrders.setProductName(singleOrderItem.getProductName());
            if(!orderRepository.existsById(uniqueID)) {
                orderRepository.save(orders);
            }
            productDetails.get().setLastOrder(new Date().getTime());
            productDetailsRepository.save(productDetails.get());
            productOrdersRepository.save(productOrders);
        }
        return ValidationDto.builder().isValid(true).reason(uniqueID).build();
    }
    public ListOfOrderDescription findOrder(String orderId) {
        Optional<Orders> orders = orderRepository.findById(orderId);
        if(orders.isPresent()) {
            return ListOfOrderDescription.builder()
                    .ordersList(Arrays.asList(orders.get()))
                    .isPresent(true)
                    .build();
        }
        return ListOfOrderDescription.builder()
                .isPresent(false)
                .reason("order not present with id")
                .build();
    }
    public ListOfOrderDescription getAllOrder(Integer pageNo, Integer pageSize) {
        if(pageNo < 0 || pageSize < 0) {
            log.error("PageNo and pagesize needs to be greater than 0");
            return ListOfOrderDescription.builder()
                    .isPresent(false)
                    .reason("PageNo and pagesize needs to be greater than 0")
                    .build();
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Orders> ordersList = orderRepository.findAll(pageable);
        if (ordersList.isEmpty()) {
            log.error("Page was empty");
            return ListOfOrderDescription.builder()
                    .isPresent(false)
                    .reason("Page was empty")
                    .build();
        }
        return ListOfOrderDescription.builder()
                .isPresent(true)
                .ordersList(ordersList.get().collect(Collectors.toList()))
                .build();
    }

    public boolean completeOrder(ChangeQuantityDto changeQuantityDto) {
        productDetails.set(productDetailsRepository.findByProductName(changeQuantityDto.getName()).get());
        if (productDetails == null) {
            log.error("Product is not present!");
        }
        Orders order = Orders.builder()
                .createdDate(new Date().getTime())
                .orderId(changeQuantityDto.getUUID())
                .build();
        Integer orderedQuantity = productOrdersRepository.findTotalQuantityOrdered(productDetails.get().getProductName());
        if(productDetails.get().getQuantity() - orderedQuantity < changeQuantityDto.getQuantity()) {
            order.setStatus("NO STOCK");
            orderRepository.save(order);
            log.error("Not enough quantity while adding");
            return false;
        }
        order.setStatus("OPEN");
        ProductOrders productOrders = new ProductOrders();
        productOrders.setQuantity(changeQuantityDto.getQuantity());
        productOrders.setOrder(order);
        productOrders.setProductName(changeQuantityDto.getName());
        if(!orderRepository.existsByOrderId(changeQuantityDto.getUUID())) {
            orderRepository.save(order);
        }
        productDetails.get().setLastOrder(new Date().getTime());
        productDetailsRepository.save(productDetails.get());
        productOrdersRepository.save(productOrders);
        return true;
    }
    public ListOfOrderDescription getByStatus(String status, Integer pageNo, Integer pageSize) {
        if(pageNo < 0 || pageSize < 0) {
            log.error("PageNo and pagesize needs to be greater than 0");
            return ListOfOrderDescription.builder()
                    .isPresent(false)
                    .reason("PageNo and pagesize needs to be greater than 0")
                    .build();
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("createdDate"));
        List<Orders> ordersList = orderRepository.findByStatus(status, pageable);
        return ListOfOrderDescription.builder()
                .isPresent(true)
                .ordersList(ordersList)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void pickItem(String productName, String orderId, Integer quantity) {
        Optional<Orders> order = orderRepository.findById(orderId);
        Optional<ProductDetails> productDetails1 = productDetailsRepository.findByProductName(productName);
        List<ProductOrders> productOrdersList = productOrdersRepository.findByOrderId(orderId);
        Optional<ProductOrders> productOrders = productOrdersRepository.findByOrderIdAndProductName(orderId, productName);
        if(!productDetails1.isPresent()) {
            log.error("Product not present!");
            return;
        }
        if(!order.isPresent()) {
            log.error("Order not present!");
            return;
        }
        if(!productOrders.isPresent()) {
            log.error("Product is not present in order!");
            return;
        }
        if(productOrders.get().isRejectedOrder()) {
            log.error("Order is already rejected! ");
            return;
        }
        if((quantity + productOrders.get().getQuantityPicked()) > productOrders.get().getQuantity()) {
            log.error("Quantity picked cannot be higher than quantity needed!");
            return;
        }
        if(quantity > productDetails1.get().getQuantity()) {
            order.get().setStatus("NO STOCK");
            productOrdersList = productOrdersList.stream().map(productOrders1 -> {
                productOrders1.setRejectedOrder(true);
                if (productOrders1.getQuantityPicked() > 0) {
                    productOrders1.setPicked(false);
                    Optional<ProductDetails> productDetailsOptional = productDetailsRepository.findByProductName(productOrders1.getProductName());
                    productDetailsOptional.get().setQuantity(productDetailsOptional.get().getQuantity() + productOrders1.getQuantityPicked());
                    productOrders1.setQuantityPicked(0);
                    return productOrders1;
                }
                return productOrders1;
            }).collect(Collectors.toList());
            log.error("quantity not enough, putting back picked items in order!");
            return;
        }
        productDetails1.get().setQuantity(productDetails1.get().getQuantity() - quantity);
        productOrders.get().setQuantityPicked(productOrders.get().getQuantityPicked() + quantity);
        if(productOrders.get().getQuantityPicked().equals(productOrders.get().getQuantity())) {
            productOrders.get().setPicked(true);
            List<ProductOrders> isAllPicked = productOrdersList.stream().filter(productOrders1 -> {
                if (productOrders1.getProductName().equals(productOrders.get().getProductName()))
                    return false;
                return !productOrders1.isPicked();
            }).collect(Collectors.toList());
            if(isAllPicked.isEmpty()) {
                order.get().setStatus("FULLY PICKED");
                return;
            }
            productOrders.get().setPicked(true);
            order.get().setStatus("PARTIALLY PICKED");
            return;
        }
        order.get().setStatus("PARTIALLY PICKED");
    }

}