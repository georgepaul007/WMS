package com.example.wms.services.servicesimpl;

import com.example.wms.dtos.ChangeQuantityDto;
import com.example.wms.dtos.ListOfOrderDescription;
import com.example.wms.dtos.ValidationDto;
import com.example.wms.entity.Orders;
import com.example.wms.entity.ProductDetails;
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
    public ValidationDto pickItem(String productName, String orderId) {
        Optional<Orders> order = orderRepository.findById(orderId);
        Optional<ProductDetails> productDetails1 = productDetailsRepository.findByProductName(productName);
        List<ProductOrders> productOrdersList = productOrdersRepository.findByOrderId(orderId);
        Optional<ProductOrders> productOrders = productOrdersRepository.findByOrderIdAndProductName(orderId, productName);
        if(!productDetails1.isPresent()) {
            log.error("Product not present!");
            return ValidationDto.builder()
                    .reason("Product Not Present")
                    .isValid(false)
                    .build();
        }
        if(!order.isPresent()) {
            log.error("Order not present!");
            return ValidationDto.builder()
                    .reason("Product Not Present")
                    .isValid(false)
                    .build();
        }
        if(productOrders.isPresent()) {
            productDetails1.get().setQuantity(productDetails1.get().getQuantity() - productOrders.get().getQuantity());
            productDetailsRepository.save(productDetails1.get());
            productOrdersRepository.delete(productOrders.get());
            productOrdersList.remove(productOrders.get());
            if(productOrdersList.isEmpty()) {
                order.get().setStatus("SUCCESSFUL");
                return ValidationDto.builder()
                        .reason("All items picked!")
                        .isValid(true)
                        .build();
            }
            order.get().setStatus("PARTIALLY PICKED");
            return ValidationDto.builder()
                    .reason("picked single item")
                    .isValid(true)
                    .build();
        }
        return ValidationDto.builder()
                .reason("Product not present in order")
                .isValid(false)
                .build();
    }

}