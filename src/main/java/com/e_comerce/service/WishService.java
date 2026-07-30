package com.e_comerce.service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.e_comerce.DTO.UserDto;
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
       Optional<UserDto.UserSummaryDto> x= wishRepo.existsByUserIdAndProductId(userId, productId);
        if (x.isEmpty()) {
        return; // already wishlisted, nothing to do —> idempotent
    }
       User user=entityManager.getReference(User.class,userId);
        Product prod=entityManager.getReference(Product.class,productId);
        wishlist wish=new wishlist(null,user,prod, LocalDateTime.now());
        wishRepo.save(wish);
    }

    public List<wishlist> GetWishes(Long UserId){
        return wishRepo.GetWishes(UserId);
    }
}
