package com.e_comerce.controller;
import java.util.List;
import com.e_comerce.DTO.OrderDto;
import com.e_comerce.DTO.UserDto;
import com.e_comerce.model.PastOrders;
import com.e_comerce.model.User;
import com.e_comerce.service.OrderService;
import com.e_comerce.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders/")
public class OrderController {

    @Autowired
    OrderService OS;
    @Autowired
    UserService us;

    @GetMapping("history")
    public Object GetOrdersHistory(){
    try {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<UserDto.UserOrderHist> History= OS.GetHistory(userId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(History);
    } catch (Exception e) {
    return ResponseEntity.badRequest().body(e);
    }
    }
    @GetMapping("all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Object GetOrdersHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
    try {
        Page<UserDto.AllOrderHist> History= OS.GetAllOrders(page, size);
        return ResponseEntity.ok(History);
    } catch (Exception e) {
    return ResponseEntity.badRequest().body(e);
    }
    }
    @GetMapping("history/bought/{HistoryNo}")
    public Object GetIndividualHistory(@PathVariable Long HistoryNo){
    try {
        List<UserDto.UserOrderItemHist> History= OS.OrderHistoryProducts(HistoryNo);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(History);
    } catch (Exception e) {
    return ResponseEntity.badRequest().body("History Of Products Bought could not Be Found");
    }
    }

    @PutMapping("status/{orderId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Object UpdateOrderStatus(@PathVariable Long orderId, @RequestBody OrderDto.UpdateStatus body){
        try {
            PastOrders order = OS.UpdateStatus(orderId, body.getStatus());
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PersistenceContext
    private EntityManager entityManager;

    @PostMapping("purchase")
    public Object BuyItems(@RequestBody OrderDto.Checkout prodsBuy){
        try{
            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = entityManager.getReference(User.class,userId);
            PastOrders history= OS.createOrder(prodsBuy,user);
            // fire and forget mail to customer
            us.SendOrderReciept(history.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(history);
            } catch (Exception e) {
           return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
