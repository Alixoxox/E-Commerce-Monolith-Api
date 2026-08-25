package com.e_comerce.service;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import com.e_comerce.model.Product;
import com.e_comerce.model.User;
import com.e_comerce.model.wishlist;
import com.e_comerce.repository.WishlistRepo;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WishServiceTest {

    @Mock
    private WishlistRepo wishlistRepo;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private WishService wishService;

    @Test
    void FetchUserWishes(){
        List<wishlist> wishes =new ArrayList<>();
        when(wishlistRepo.GetWishes(any(Long.class))).thenReturn(wishes);
        wishService.GetWishes(1L);
        verify(wishlistRepo).GetWishes(1L);
    }

    @Test
    void FetchNullUserWishes(){
        when(wishlistRepo.GetWishes(any(Long.class))).thenReturn(null);
        List<wishlist> wishes = wishService.GetWishes(1L);
        assertNull(wishes);
        verify(wishlistRepo).GetWishes(1L);
    }
    @Test
    void FetchUserWishes_InvalidUserId() {
    assertThrows(
        IllegalArgumentException.class,
        () -> wishService.GetWishes(null)
    );
    verifyNoInteractions(wishlistRepo);
    }

    @Test // already marked
    void markUserWishAlreadyMarked(){
        Boolean status = true;
        when(wishlistRepo.existsByUser_IdAndProduct_Id(1L,1L)).thenReturn(status);
        wishService.MarkWish(1L,1L);
        verify(wishlistRepo).deleteByUser_IdAndProduct_Id(1L,1L);
    }

    @Test //not marked
    void markUserWishWithProduct() {
        Boolean status = false;
        when(wishlistRepo.existsByUser_IdAndProduct_Id(1L, 1L))
                .thenReturn(status);

        // condition failed (no prev wish wrt user and prod)-> mark wishing
        when(entityManager.getReference(Product.class,1L)).thenReturn(new Product());
        when(entityManager.getReference(User.class,1L)).thenReturn(new User());

        wishService.MarkWish(1L, 1L);
        verify(wishlistRepo).save(any(wishlist.class));
    }
    @Test
    void markUserWishWithNoProduct() {
    assertThrows(
        IllegalArgumentException.class,
        () -> wishService.MarkWish(1L, null)
    );

    verifyNoInteractions(wishlistRepo);
    verifyNoInteractions(entityManager);
    }
    @Test
    void markProductForNullUser() {
    assertThrows(
        IllegalArgumentException.class,
        () -> wishService.MarkWish(null, 1L)
    );

    verifyNoInteractions(wishlistRepo);
    verifyNoInteractions(entityManager);
    }
}
