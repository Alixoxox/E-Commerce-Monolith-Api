package com.e_comerce.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import com.e_comerce.DTO.OrderDto;
import com.e_comerce.DTO.UserDto;
import com.e_comerce.model.OrderItems;
import com.e_comerce.model.PastOrders;
import com.e_comerce.model.Product;
import com.e_comerce.model.User;
import com.e_comerce.model.enums.OrderStatus;
import com.e_comerce.repository.PastOrderRepo;
import com.e_comerce.repository.ProductPriceView;
import com.e_comerce.repository.ProductRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private ProductRepo Pr;
    @Autowired
    private PastOrderRepo OPR;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private ProdService PS;

    private static final BigDecimal TAX_RATE = new BigDecimal("0.05"); // 5%

@SneakyThrows
@Transactional
@Caching(evict = {
    @CacheEvict(value = "UserOrderHistory", key = "#user.id"),
    @CacheEvict("AdminGetAllOrders")
})
public PastOrders createOrder(OrderDto.Checkout prods, User user) {

    List<Long> ids = new ArrayList<>();
    Map<Long, Integer> qty = new HashMap<>();

    for (OrderDto.Item item : prods.getProducts()) {
        ids.add(item.getId());
        qty.put(item.getId(), item.getQuantity());
    }
    Collections.sort(ids);

    List<ProductPriceView> priceInfo = Pr.findByIdIn(ids);
    if (priceInfo.size() != ids.size()) {
        throw new RuntimeException("One or more products not found.\nThey might be Out Of Stock.");
    }

    PastOrders pastOrders = new PastOrders(null,user,LocalDateTime.now(),null,prods.getPhoneNumber(),prods.getCity(),prods.getCountry(),prods.getPostalCode(),prods.getAddress(),OrderStatus.PENDING,null);

    List<OrderItems> items = new ArrayList<>();
    BigDecimal subTotal = BigDecimal.ZERO;

    for (ProductPriceView p : priceInfo) {
        int q = qty.get(p.getId());

        Product productRef = entityManager.getReference(Product.class, p.getId());

        if (Pr.decrementStock(p.getId(), q) == 0) {
            throw new RuntimeException("Not enough stock for Product " + productRef.getTitle());
        }

        OrderItems item = new OrderItems();
        item.setPastOrder(pastOrders);   // links each item back to this order
        item.setProduct(productRef);
        item.setQuantity(q);
        item.setPriceAtPurchase(p.getPrice());
        items.add(item);
        subTotal = subTotal.add(p.getPrice().multiply(BigDecimal.valueOf(q)));
    }

    pastOrders.setOrderItems(items);       // attach the not-yet-saved list here

    BigDecimal taxAmount = subTotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
    BigDecimal totalAmount = subTotal.add(taxAmount);

    pastOrders.setTotalAmount(totalAmount);

    return OPR.save(pastOrders); // cascades and saves everything in one go
    }
    @Cacheable(value="UserOrderHistory",key = "#userId")
    public List<UserDto.UserOrderHist> GetHistory(Long userId){
    return OPR.findOrderByUserId(userId);
    }
    @Cacheable("AdminGetAllOrders")
    public Page<UserDto.AllOrderHist> GetAllOrders(int page, int size){
    PageRequest pageable = PageRequest.of(page, size);
    return OPR.findAllOrders(pageable);
    }
    @Cacheable(value="OrderHistoryProductsDetail",key = "#HistoryId")
    public List<UserDto.UserOrderItemHist> OrderHistoryProducts(Long HistoryId){
    return  convertToViewAbleImage(OPR.findOrderItemRaw(HistoryId));
    }

    @Transactional
    public PastOrders UpdateStatus(Long orderId, OrderStatus status){
        PastOrders order = entityManager.find(PastOrders.class, orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        order.setStatus(status);
        return entityManager.merge(order);
    }
    private List<UserDto.UserOrderItemHist> convertToViewAbleImage(List<UserDto.UserOrderItemHist> history) {
    return history.stream().map(product -> convertToViewAbleImage(product)).toList();
    }
    private UserDto.UserOrderItemHist convertToViewAbleImage(UserDto.UserOrderItemHist p) {
        if(p==null){
            return null;
        }
        return new UserDto.UserOrderItemHist(p.getId(),p.getTitle(),PS.convertImg(p.getImage()),p.getCategory(),p.getQuantity(),p.getPriceAtPurchase());
    }

}

