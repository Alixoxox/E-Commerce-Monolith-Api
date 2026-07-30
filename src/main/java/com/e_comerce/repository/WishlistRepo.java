package com.e_comerce.repository;

import java.util.List;

import com.e_comerce.model.User;
import com.e_comerce.model.wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WishlistRepo extends JpaRepository<wishlist,Long> {
    @Query("Select p from wishlist w join product p where w.user.id = :user ")
    List<wishlist> GetWishes(@Param("user") Long user);
}
