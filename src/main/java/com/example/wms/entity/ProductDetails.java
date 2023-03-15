package com.example.wms.entity;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "product_details")
public class ProductDetails implements Serializable {

        @Column(name = "quantity")
        Integer quantity;

        @Id
        @Column(name = "product_name", unique = true)
        String productName;

        @NotNull
        @Column(name = "merchant_id")
        String merchantId;

        @Column(name = "price")
        @NotNull
        Double price;

        @Column(name = "last_order")
        Long lastOrder;

        @Column(name = "last_incoming_goods")
        Long lastIncomingGoods;


}
