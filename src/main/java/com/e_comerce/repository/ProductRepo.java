package com.e_comerce.repository;

import java.util.Collection;
import java.util.List;

import com.e_comerce.model.Product;
import com.e_comerce.model.enums.category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepo extends JpaRepository<Product, Long> {
    @Query("SELECT DISTINCT p.category FROM Product p")
    List<String> GetCategory();

    @Query("SELECT p from Product p where p.category = :cat")
    List<Product> GetProdCategories(@Param("category") category cat);

    List<ProductPriceView> findByIdIn(Collection<Long> ids);

    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :qty WHERE p.id = :id AND p.stock >= :qty")
    int decrementStock(@Param("id") Long id, @Param("qty") Integer qty);
}