package com.e_comerce.repository;

import com.e_comerce.model.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepo extends CrudRepository<Product, Long> {
}
