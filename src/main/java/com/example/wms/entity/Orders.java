package com.example.wms.entity;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Orders implements Serializable {

    @Id
    @Column(name = "order_id")
    private String orderId;

    @Column(name = "created_date")
    @NotNull
    private Long createdDate;

    @Column(name = "status")
    private String status;



}
