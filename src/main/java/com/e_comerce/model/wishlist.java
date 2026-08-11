package com.e_comerce.model;

import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    @ToString.Exclude
    private User user;

    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference
    @ToString.Exclude
    private Product product;

    public Long getProductId() {
        return product != null ? product.getId() : null;
    }
    private LocalDateTime addedAt;

}
