package com.e_comerce.repository;

import com.e_comerce.model.PastOrders;
import org.springframework.data.repository.CrudRepository;

public interface PastOrderRepo extends CrudRepository<PastOrders, Long> {
}
