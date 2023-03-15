package com.example.wms.repo;

import com.example.wms.entity.ProductDetails;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductDetailsRepository extends CrudRepository<ProductDetails, String> {
    Optional<ProductDetails> findByProductName(String productName);

}
