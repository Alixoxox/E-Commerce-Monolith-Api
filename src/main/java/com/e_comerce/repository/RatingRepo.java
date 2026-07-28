package com.e_comerce.repository;

import com.e_comerce.model.rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepo extends JpaRepository<rating, Long> {
}
