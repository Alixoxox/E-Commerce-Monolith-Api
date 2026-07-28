package com.e_comerce.repository;

import com.e_comerce.model.rating;
import org.springframework.data.repository.CrudRepository;

public interface RatingRepo extends CrudRepository<rating, Long> {
}
