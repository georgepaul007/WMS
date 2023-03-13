package com.example.wms.services.servicesimpl;

import com.example.wms.dtos.ChangeQuantityDto;
import com.example.wms.dtos.ListOfAddStock;
import com.example.wms.dtos.ListOfOrderItem;
import com.example.wms.dtos.SingleOrderItem;
import com.example.wms.dtos.ValidationDto;
import com.example.wms.entity.IncomingGoods;
import com.example.wms.entity.ProductDetails;
import com.example.wms.entity.ProductIncoming;
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
    @Value("${topic.name.edit-product-details}")
    private String topicName;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private IncomingGoodsRepository incomingGoodsRepository;
    @Autowired
    private ProductIncomingRepository productIncomingRepository;
    @Autowired
    private ProductDetailsRepository productDetailsRepository;
    public static AtomicReference<ProductDetails> productDetails = new AtomicReference<>();
    public ValidationDto createIncomingGoodsOrOrder(ListOfOrderItem listOfOrderItem, String orderOrIG) {
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
            if (orderOrIG.equals("incoming")) {
                log.info("Sending kafka incoming!");
                kafkaTemplate.send(topicName, singleOrderItem.getProductName(), ChangeQuantityDto.builder().UUID(uniqueID).name(singleOrderItem.getProductName()).quantity(singleOrderItem.getQuantity()).incomingOrOrder("incoming").build());
            } else {
                log.info("Sending kafka Order!");
                kafkaTemplate.send(topicName, singleOrderItem.getProductName(), ChangeQuantityDto.builder().incomingOrOrder("order").UUID(uniqueID).name(singleOrderItem.getProductName()).quantity(singleOrderItem.getQuantity()).build());
            }
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
        productDetails.set(productDetailsRepository.findByProductName(changeQuantityDto.getName()).get());
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
    public ValidationDto putawayItem(String productName, String incomingGoodsId) {
        Optional<IncomingGoods> incomingGoods = incomingGoodsRepository.findById(incomingGoodsId);
        Optional<ProductDetails> productDetails1 = productDetailsRepository.findByProductName(productName);
        Optional<ProductIncoming> productIncoming = productIncomingRepository.findByIncomingIdAndProductName(incomingGoodsId, productName);
        List<ProductIncoming> productIncomingList = productIncomingRepository.findByIncomingId(incomingGoodsId);
        if(!productDetails1.isPresent()) {
            log.error("Product not present!");
            return ValidationDto.builder()
                    .reason("Product Not Present")
                    .isValid(false)
                    .build();
        }
        if(!incomingGoods.isPresent()) {
            log.error("Order not present!");
            return ValidationDto.builder()
                    .reason("Product Not Present")
                    .isValid(false)
                    .build();
        }
        if(productIncoming.isPresent()) {
            productDetails1.get().setQuantity(productDetails1.get().getQuantity() + productIncoming.get().getQuantity());
            productDetailsRepository.save(productDetails1.get());
            productIncomingRepository.delete(productIncoming.get());
            productIncomingList.remove(productIncoming.get());
            if(productIncomingList.isEmpty()) {
                incomingGoods.get().setStatus("PACKED ALL");
                return ValidationDto.builder()
                        .reason("All items put away!")
                        .isValid(true)
                        .build();
            }
            incomingGoods.get().setStatus("PARTIALLY PUTAWAY");
            return ValidationDto.builder()
                    .reason("put away single item")
                    .isValid(true)
                    .build();
        }
        return ValidationDto.builder()
                .reason("Product not present in order")
                .isValid(false)
                .build();
    }
}