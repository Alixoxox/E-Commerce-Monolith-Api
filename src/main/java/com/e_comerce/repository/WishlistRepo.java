package com.e_comerce.repository;

import com.e_comerce.model.wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepo extends JpaRepository<wishlist,Long> {
}
