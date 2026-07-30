package com.e_comerce.repository;

import java.util.List;

import com.e_comerce.model.rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RatingRepo extends JpaRepository<rating, Long> {
    @Query("select avg(r.value) from rating r where r.product.id = :prodId")
    Double getAvgRating(@Param("prodId") Long prodId);
}
