package com.e_comerce.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class OrderItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private PastOrders pastOrder;

    @ManyToOne
    @JoinColumn(name="product_id")
    @JsonBackReference
    private Product product;

    private Integer quantity;

    private BigDecimal priceAtPurchase;
}
