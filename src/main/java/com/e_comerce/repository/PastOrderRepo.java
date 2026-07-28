package com.e_comerce.repository;

import com.e_comerce.model.PastOrders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PastOrderRepo extends JpaRepository<PastOrders, Long> {
}
