package com.e_comerce.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.e_comerce.model.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    @JsonBackReference
    @ToString.Exclude
    private User user;

    private LocalDateTime orderDate;

    private BigDecimal totalAmount;

    private String phoneNumber;
    private String city;
    private String country;
    private String postalCode;
    private String address;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "pastOrder", cascade = CascadeType.ALL)
    @JsonManagedReference
    @ToString.Exclude
    private List<OrderItems> orderItems;
}
