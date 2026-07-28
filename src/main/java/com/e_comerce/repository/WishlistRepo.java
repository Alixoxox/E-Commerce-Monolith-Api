package com.e_comerce.repository;

import com.e_comerce.model.wishlist;
import org.springframework.data.repository.CrudRepository;

public interface WishlistRepo extends CrudRepository<wishlist,Long> {
}
