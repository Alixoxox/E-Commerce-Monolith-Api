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
import org.springframework.stereotype.Service;

@Service
public class WishService {

    @Autowired
    private WishlistRepo wishRepo;
    @PersistenceContext
    private EntityManager entityManager;

    @SneakyThrows
    @Transactional
    public void MarkWish(Long userId,Long productId){
        if (wishRepo.existsByUser_IdAndProduct_Id(userId, productId)) {
            wishRepo.deleteByUser_IdAndProduct_Id(userId, productId);
            System.out.println("Unwished");
            return;
        }
        User user=entityManager.getReference(User.class,userId);
        Product prod=entityManager.getReference(Product.class,productId);
        wishlist wish=new wishlist(null,user,prod, LocalDateTime.now());
        wishRepo.save(wish);
        System.out.println("wished for it");
    }
    @SneakyThrows
    public List<wishlist> GetWishes(Long UserId){
        List<wishlist> wishing =wishRepo.GetWishes(UserId);
        return wishing;
    }
}
