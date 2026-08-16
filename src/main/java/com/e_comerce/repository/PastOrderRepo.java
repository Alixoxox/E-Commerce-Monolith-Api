package com.e_comerce.repository;

import java.util.List;

import com.e_comerce.DTO.UserDto;
import com.e_comerce.model.PastOrders;
import com.e_comerce.model.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.e_comerce.DTO.UserDto.UserOrderItemHist;


public interface PastOrderRepo extends JpaRepository<PastOrders, Long> {
    @Query("SELECT new com.e_comerce.DTO.UserDto$UserOrderHist(p.id, p.orderDate, p.totalAmount, p.phoneNumber, p.city, p.country, p.postalCode, p.address, p.status) FROM PastOrders p WHERE p.user.id = :userId")
    List<UserDto.UserOrderHist> findOrderByUserId(@Param("userId") Long userId);

    @Query("SELECT new com.e_comerce.DTO.UserDto$AllOrderHist(p.id, p.orderDate, p.totalAmount, p.phoneNumber, p.city, p.country, p.postalCode, p.address, p.status, p.user.name,p.user.email) FROM PastOrders p")
    Page<UserDto.AllOrderHist> findAllOrders(Pageable page);

    @Query("SELECT new com.e_comerce.DTO.UserDto$UserOrderItemHist(o.product.id,o.product.title, o.product.image,o.product.category,o.quantity,o.priceAtPurchase) FROM OrderItems o WHERE o.pastOrder.id = :historyId")
    List<UserOrderItemHist> findOrderItemRaw(@Param("historyId") Long historyId);

    @Modifying
    @Query("Update PastOrders p Set p.status = status where p.id = :id")
    boolean updateOrderStatus(@Param("status") OrderStatus status,@Param("id") Long id);
}
