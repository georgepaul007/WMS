package com.example.wms.services.servicesimpl;

import com.example.wms.dtos.ChangeQuantityDto;
import com.example.wms.dtos.ListOfAddStock;
import com.example.wms.dtos.ListOfOrderItem;
import com.example.wms.dtos.SingleOrderItem;
import com.example.wms.dtos.ValidationDto;
import com.example.wms.entity.IncomingGoods;
import com.example.wms.entity.Orders;
import com.example.wms.entity.ProductDetails;
import com.example.wms.entity.ProductIncoming;
import com.example.wms.entity.ProductOrders;
import com.example.wms.repo.IncomingGoodsRepository;
import com.example.wms.repo.ProductDetailsRepository;
import com.example.wms.repo.ProductIncomingRepository;
import com.example.wms.services.IncomingGoodsServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
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
public class IncomingGoodsServicesImpl implements IncomingGoodsServices {
    @Autowired
    private IncomingGoodsRepository incomingGoodsRepository;
    @Autowired
    private ProductIncomingRepository productIncomingRepository;
    @Autowired
    private ProductDetailsRepository productDetailsRepository;
    public static AtomicReference<ProductDetails> productDetails = new AtomicReference<>();
    public ValidationDto createIncomingGoods(ListOfOrderItem listOfOrderItem) {
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
            IncomingGoods incomingGoods = IncomingGoods.builder()
                    .createdDate(new Date().getTime())
                    .incomingGoodsId(uniqueID)
                    .build();
            incomingGoods.setStatus("SHIPPED");
            ProductIncoming productIncoming = new ProductIncoming();
            productIncoming.setQuantity(singleOrderItem.getQuantity());
            productIncoming.setIncoming(incomingGoods);
            productIncoming.setProductName(singleOrderItem.getProductName());
            if(!incomingGoodsRepository.existsById(uniqueID)) {
                incomingGoodsRepository.save(incomingGoods);
            }
            productDetails.get().setLastIncomingGoods(new Date().getTime());
            productDetailsRepository.save(productDetails.get());
            productIncomingRepository.save(productIncoming);
        }
        return ValidationDto.builder().isValid(true).reason(uniqueID).build();
    }

    public ListOfAddStock findIncomingGoods(String incomingGoodsId) {
        Optional<IncomingGoods> incomingGoods = incomingGoodsRepository.findById(incomingGoodsId);
        if(incomingGoods.isPresent()) {
            return ListOfAddStock.builder()
                    .incomingGoodsList(Arrays.asList(incomingGoods.get()))
                    .isPresent(true)
                    .build();
        }
        return ListOfAddStock.builder()
                .isPresent(false)
                .reason("order not present with id")
                .build();
    }

    public ListOfAddStock getAllIncomingGoods(Integer pageNo, Integer pageSize) {
        if(pageNo < 0 || pageSize < 0) {
            log.error("PageNo and pagesize needs to be greater than 0");
            return ListOfAddStock.builder()
                    .isPresent(false)
                    .reason("PageNo and pagesize needs to be greater than 0")
                    .build();
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<IncomingGoods> incomingGoodsPage = incomingGoodsRepository.findAll(pageable);
        if (incomingGoodsPage.isEmpty()) {
            log.error("Page was empty");
            return ListOfAddStock.builder()
                    .isPresent(false)
                    .reason("Page was empty")
                    .build();
        }
        return ListOfAddStock.builder()
                .isPresent(true)
                .incomingGoodsList(incomingGoodsPage.get().collect(Collectors.toList()))
                .build();
    }
    public boolean completeIncomingGoods(ChangeQuantityDto changeQuantityDto) {
        productDetails.set(productDetailsRepository
                .findByProductName(changeQuantityDto.getName()).get());
        if (productDetails == null) {
            log.error("Product is not present!");
        }
        IncomingGoods incomingGoods = IncomingGoods.builder()
                .createdDate(new Date().getTime())
                .incomingGoodsId(changeQuantityDto.getUUID())
                .build();
        incomingGoods.setStatus("SHIPPED");
        ProductIncoming productIncoming = new ProductIncoming();
        productIncoming.setQuantity(changeQuantityDto.getQuantity());
        productIncoming.setIncoming(incomingGoods);
        productIncoming.setProductName(changeQuantityDto.getName());
        if(!incomingGoodsRepository.existsById(changeQuantityDto.getUUID())) {
            incomingGoodsRepository.save(incomingGoods);
        }
        productDetails.get().setLastIncomingGoods(new Date().getTime());
        productDetailsRepository.save(productDetails.get());
        productIncomingRepository.save(productIncoming);
        return true;
    }
    @Transactional(rollbackFor = Exception.class)
    public void putawayItem(String productName, String incomingGoodsId, Integer quantity) {
        Optional<IncomingGoods> incomingGoods = incomingGoodsRepository.findById(incomingGoodsId);
        Optional<ProductDetails> productDetails1 = productDetailsRepository.findByProductName(productName);
        List<ProductIncoming> productIncomingList = productIncomingRepository.findByIncomingId(incomingGoodsId);
        Optional<ProductIncoming> productIncoming = productIncomingRepository.findByIncomingIdAndProductName(incomingGoodsId, productName);
        if(!productDetails1.isPresent()) {
            log.error("Product not present!");
            return;
        }
        if(!incomingGoods.isPresent()) {
            log.error("Incoming Goods not present!");
        }
        if(!productIncoming.isPresent()) {
            log.error("Product is not present in incoming goods!");
            return;
        }
        if((quantity + productIncoming.get().getQuantityPutaway()) > productIncoming.get().getQuantity()) {
            log.error("Quantity putaway cannot be higher than quantity needed!");
            return;
        }
        productDetails1.get().setQuantity(productDetails1.get().getQuantity() + quantity);
        productIncoming.get().setQuantityPutaway(productIncoming.get().getQuantityPutaway() + quantity);
        if(productIncoming.get().getQuantityPutaway().equals(productIncoming.get().getQuantity())) {
            productIncoming.get().setPutaway(true);
            List<ProductIncoming> isAllPutaway = productIncomingList.stream().filter(productIncoming1 -> {
                if (productIncoming1.getProductName().equals(productIncoming.get().getProductName()))
                    return false;
                return !productIncoming1.isPutaway();
            }).collect(Collectors.toList());
            if(isAllPutaway.isEmpty()) {
                incomingGoods.get().setStatus("FULLY PUTAWAY");
                return;
            }
            productIncoming.get().setPutaway(true);
            incomingGoods.get().setStatus("PARTIALLY PUTAWAY");
            return;
        }
        incomingGoods.get().setStatus("PARTIALLY PUTAWAY");
    }
}