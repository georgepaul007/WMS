package com.example.wms.repo;

import com.example.wms.entity.IncomingGoods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomingGoodsRepository extends CrudRepository<IncomingGoods, String> {
    IncomingGoods findByIncomingGoodsId(String incomingGoodsId);
    Page<IncomingGoods> findAll(Pageable pageable);

}
