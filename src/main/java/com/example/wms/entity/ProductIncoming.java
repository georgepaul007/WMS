package com.example.wms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProductIncoming {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String productName;

    @ManyToOne(targetEntity = IncomingGoods.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "incoming_goods_id", referencedColumnName = "incoming_goods_id")
    private IncomingGoods incoming;

    @Column(name = "quantity")
    private Integer quantity;


    @Column(name = "is_picked")
    private boolean isPutaway = false;

    @Column(name = "quantity_picked")
    private Integer quantityPutaway;
}
