package com.e_comerce.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    @ToString.Exclude
    private PastOrders pastOrder;

    @ManyToOne
    @JoinColumn(name="product_id")
    @JsonBackReference
    @ToString.Exclude
    private Product product;

    private Integer quantity;

    private BigDecimal priceAtPurchase;

    public BigDecimal getSubtotal() {
        return priceAtPurchase.multiply(BigDecimal.valueOf(quantity.longValue()));}
}