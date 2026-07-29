package com.e_comerce.model;

import java.math.BigDecimal;
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
    private PastOrders pastOrder;

    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;

    private Integer quantity;

    private BigDecimal priceAtPurchase;
}
