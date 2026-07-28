package com.e_comerce.repository;

import com.e_comerce.model.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepo extends JpaRepository<OrderItems, Long> {
}
