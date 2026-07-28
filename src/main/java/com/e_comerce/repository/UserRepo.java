package com.e_comerce.repository;

import com.e_comerce.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<User, Long> {
}
