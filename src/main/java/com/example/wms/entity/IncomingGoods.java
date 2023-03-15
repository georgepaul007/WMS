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
@Table
public class IncomingGoods implements Serializable {

    @NotNull
    @Column( name = "incoming_goods_id", unique = true)
    @Id
    private String incomingGoodsId;

    @NotNull
    @Column( name = "created_date")
    private Long createdDate;

    @Column(name = "status")
    private String status;


}
