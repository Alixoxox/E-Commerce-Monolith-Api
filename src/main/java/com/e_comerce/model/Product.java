package com.e_comerce.model;

import java.math.BigDecimal;
import java.util.List;

import com.e_comerce.model.enums.category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private category category;

    private String image;
    private Integer stock;
    private BigDecimal price;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<wishlist> wishlistedBy;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<rating> ratings;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<OrderItems> orderItems;
}