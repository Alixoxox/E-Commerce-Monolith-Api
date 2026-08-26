package com.e_comerce.service;

import java.time.LocalDateTime;
import java.util.List;
import com.e_comerce.model.Product;
import com.e_comerce.model.User;
import com.e_comerce.model.wishlist;
import com.e_comerce.repository.WishlistRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class WishService {

    @Autowired
    private WishlistRepo wishRepo;
    @PersistenceContext
    private EntityManager entityManager;

    @SneakyThrows
    @Transactional
    @CacheEvict(value="Wishlist",key="#userId")
    public void MarkWish(Long userId,Long productId){
        if (userId == null || productId == null) {
        throw new IllegalArgumentException("User ID and Product ID cannot be null");
        }
        if (wishRepo.existsByUser_IdAndProduct_Id(userId, productId)) {
            wishRepo.deleteByUser_IdAndProduct_Id(userId, productId);
            return;
        }
        User user=entityManager.getReference(User.class,userId);
        Product prod=entityManager.getReference(Product.class,productId);
        wishlist wish=new wishlist(null,user,prod, LocalDateTime.now());
        wishRepo.save(wish);
    }
    @SneakyThrows
    @Cacheable(value = "Wishlist",key = "#UserId")
    public List<wishlist> GetWishes(Long UserId){
        if (UserId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        List<wishlist> wishing =wishRepo.GetWishes(UserId);
        return wishing;
    }
}
