package com.e_comerce.service;

import java.time.LocalDateTime;
import java.util.List;

import com.e_comerce.model.Product;
import com.e_comerce.model.User;
import com.e_comerce.model.wishlist;
import com.e_comerce.repository.WishlistRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WishService {

    @Autowired
    private WishlistRepo wishRepo;

    @PersistenceContext
    private EntityManager entityManager;

    @SneakyThrows
    public void MarkWish(Long userId,Long productId){
       User user=entityManager.getReference(User.class,userId);
        Product prod=entityManager.getReference(Product.class,productId);
        wishlist wish=new wishlist(null,user,prod, LocalDateTime.now());
        wishRepo.save(wish);
    }

    public List<wishlist> GetWishes(Long UserId){
        return wishRepo.GetWishes(UserId);
    }
}
