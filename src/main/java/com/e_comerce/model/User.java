package com.e_comerce.model;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private LocalDateTime created_at;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<rating> ratings;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL)
    private List<Cart> cartItems;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<wishlist> wishlistItems;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PastOrders> pastOrders;

}
