package com.e_comerce.repository;

import com.e_comerce.model.Cart;
import org.springframework.data.repository.CrudRepository;

public interface CartRepo extends CrudRepository<Cart, Long> {
}
