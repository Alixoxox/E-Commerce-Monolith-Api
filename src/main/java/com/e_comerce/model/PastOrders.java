package com.e_comerce.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.e_comerce.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name="past_orders")
public class PastOrders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime orderDate;

    private BigDecimal totalAmount;

    private OrderStatus status;

    @OneToMany(mappedBy = "pastOrder", cascade = CascadeType.ALL)
    private List<OrderItems> orderItems;
}
