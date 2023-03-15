package com.example.wms.repo;

import com.example.wms.entity.ProductIncoming;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductIncomingRepository extends CrudRepository<ProductIncoming, Long> {
    @Query(value = "SELECT * FROM product_incoming WHERE incoming_goods_id = :incomingGoodsId AND product_name = :productName", nativeQuery = true)
    Optional<ProductIncoming> findByIncomingIdAndProductName(@Param("incomingGoodsId") String incomingGoodsId, @Param("productName") String productName);

    @Query(value = "SELECT * FROM product_incoming WHERE incoming_goods_id = :incomingId", nativeQuery = true)
    List<ProductIncoming> findByIncomingId(@Param("incomingId") String incomingId);
}
