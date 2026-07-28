package com.e_comerce.repository;

import com.e_comerce.model.OrderItems;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemRepo extends CrudRepository<OrderItems, Long> {
}
