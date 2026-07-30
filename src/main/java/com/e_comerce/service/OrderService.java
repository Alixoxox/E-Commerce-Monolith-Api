package com.e_comerce.service;

import javax.naming.InsufficientResourcesException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import com.e_comerce.DTO.OrderDto;
import com.e_comerce.model.OrderItems;
import com.e_comerce.model.PastOrders;
import com.e_comerce.model.Product;
import com.e_comerce.model.User;
import com.e_comerce.model.enums.OrderStatus;
import com.e_comerce.repository.OrderItemRepo;
import com.e_comerce.repository.PastOrderRepo;
import com.e_comerce.repository.ProductPriceView;
import com.e_comerce.repository.ProductRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private ProductRepo Pr;
    @Autowired
    private PastOrderRepo OPR;
    @Autowired
    private OrderItemRepo OIR;

    @PersistenceContext
    private EntityManager entityManager;

@SneakyThrows
@Transactional
public PastOrders createOrder(List<OrderDto.Item> prods, Long userId) {

    List<Long> ids = new ArrayList<>();
    Map<Long, Integer> qty = new HashMap<>();

    for (OrderDto.Item item : prods) {
        ids.add(item.getId());
        qty.put(item.getId(), item.getQuantity());
    }
    Collections.sort(ids);

    List<ProductPriceView> priceInfo = Pr.findByIdIn(ids);
    if (priceInfo.size() != ids.size()) {
        throw new RuntimeException("One or more products not found.\nThey might be Out Of Stock.");
    }

    User user = entityManager.getReference(User.class, userId);

    PastOrders pastOrders = new PastOrders();
    pastOrders.setUser(user);
    pastOrders.setOrderDate(LocalDateTime.now());
    pastOrders.setStatus(OrderStatus.PENDING);

    List<OrderItems> items = new ArrayList<>();
    BigDecimal totalAmount = BigDecimal.ZERO;

    for (ProductPriceView p : priceInfo) {
        int q = qty.get(p.getId());

        if (Pr.decrementStock(p.getId(), q) == 0) {
            throw new InsufficientResourcesException("Not enough stock for product " + p.getId());
        }

        Product productRef = entityManager.getReference(Product.class, p.getId());

        OrderItems item = new OrderItems();
        item.setPastOrder(pastOrders);   // links each item back to this order
        item.setProduct(productRef);
        item.setQuantity(q);
        item.setPriceAtPurchase(p.getPrice());
        items.add(item);

        totalAmount = totalAmount.add(p.getPrice().multiply(BigDecimal.valueOf(q)));
    }

    pastOrders.setOrderItems(items);       // attach the not-yet-saved list here
    pastOrders.setTotalAmount(totalAmount);

    return OPR.save(pastOrders); // cascades and saves everything in one go
    }

    public Page<PastOrders> GetHistory(Long userId, Pageable page){
    User user=entityManager.getReference(User.class,userId);
    return OPR.findByUserId(user,page);
    }

    public List<OrderItems> OrderHistoryProducts(Long userId,Long HistoryId){
    return OPR.findByIdAndUser(HistoryId, userId);
    }

}

