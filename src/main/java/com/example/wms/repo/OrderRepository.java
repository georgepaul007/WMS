package com.example.wms.repo;

import com.example.wms.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Orders, String> {
    Page<Orders> findAll(Pageable pageable);

//    @Query("select orders.productDetails from Orders orders " +
//            "INNER JOIN orders.productDetails pd " +
//            "WHERE orders.orderId = :orderId")
//    Set<ProductDetails> findProductDetailsByOrderId(@Param("orderId") String orderId);

    Boolean existsByOrderId(String orderId);

    List<Orders> findByStatus(String status, Pageable pageable);
}
