package com.e_comerce.repository;

import java.util.List;

import com.e_comerce.model.OrderItems;
import com.e_comerce.model.PastOrders;
import com.e_comerce.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PastOrderRepo extends JpaRepository<PastOrders, Long> {
    Page<PastOrders> findByUserId(User user, Pageable pageable);

    @Query("SELECT o FROM PastOrders o JOIN FETCH o.orderItems oi JOIN FETCH oi.product WHERE o.id = :HistoryId AND o.user = :user")
    List<OrderItems> findByIdAndUser(@Param("HistoryId") Long HistoryId, @Param("user") User user);

}
