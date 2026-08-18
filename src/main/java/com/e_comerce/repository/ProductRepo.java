package com.e_comerce.repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.e_comerce.DTO.productDTOs;
import com.e_comerce.model.Product;
import com.e_comerce.model.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepo extends JpaRepository<Product, Long> {

    @Query("""
        SELECT new com.e_comerce.DTO.productDTOs$ProductSummary(
            p.id, p.title, p.price, p.description, p.category, p.image, p.stock,
            COALESCE(AVG(r.value), 0),
            COUNT(r)
        ) FROM Product p
        LEFT JOIN p.ratings r
        WHERE (:search = '' OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:category IS NULL OR p.category = :category)
        GROUP BY p.id, p.title, p.price, p.description, p.category, p.image, p.stock
        """)
    Page<productDTOs.ProductSummary> getSummary(Pageable pageable, @Param("search") String search, @Param("category") Category category);

    @Query("SELECT p from Product p where p.category = :cat")
    List<Product> GetProdCategories(@Param("cat") Category cat);

    List<ProductPriceView> findByIdIn(Collection<Long> ids);

    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :qty WHERE p.id = :id AND p.stock >= :qty")
    int decrementStock(@Param("id") Long id, @Param("qty") Integer qty);

    @Query("SELECT p.title FROM Product p WHERE p.title IN :titles")
    Set<String> findExistingTitles(@Param("titles") Set<String> titles);
}