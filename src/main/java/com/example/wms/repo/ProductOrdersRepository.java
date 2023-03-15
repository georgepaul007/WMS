package com.example.wms.repo;

import com.example.wms.entity.ProductOrders;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOrdersRepository extends CrudRepository<ProductOrders, Long> {
    @Query("SELECT COALESCE(SUM(quantity), 0) FROM ProductOrders WHERE product_name = :productName")
    Integer findTotalQuantityOrdered(@Param("productName") String productName);


    @Query(value = "SELECT * FROM product_orders WHERE order_id = :orderId AND product_name = :productName", nativeQuery = true)
    Optional<ProductOrders> findByOrderIdAndProductName(@Param("orderId") String orderId, @Param("productName") String productName);
    //    Set<ProductDetails> findProductDetailsByOrderId(@Param("orderId") String orderId);

    @Query(value = "SELECT * FROM product_orders WHERE order_id = :orderId", nativeQuery = true)
    List<ProductOrders> findByOrderId(@Param("orderId") String orderId);

}
