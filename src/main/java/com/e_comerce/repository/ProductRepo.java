package com.e_comerce.repository;

import java.util.List;

import com.e_comerce.model.Product;
import com.e_comerce.model.enums.category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepo extends JpaRepository<Product, Long> {
    @Query("SELECT DISTINCT p.category FROM Product p")
    public List<String> GetCategory();

    @Query("SELECT p from Product p where p.category = :cat")
    public List<Product> GetProdCategories(@Param("category") category cat);
}
