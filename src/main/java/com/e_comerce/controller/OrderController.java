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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders/")
public class OrderController {

    @Autowired
    OrderService OS;
    @Autowired
    UserService us;
    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("history")
    public Object GetOrdersHistory(){
    try {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<UserDto.UserOrderHist> History= OS.GetHistory(userId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(History);
    } catch (Exception e) {
        e.printStackTrace();
    return ResponseEntity.badRequest().body(e);
    }
    }

    @GetMapping("history/bought/{HistoryNo}")
    public Object GetIndividualHistory(@PathVariable Long HistoryNo){
    try {
        List<UserDto.UserOrderItemHist> History= OS.OrderHistoryProducts(HistoryNo);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(History);
    } catch (Exception e) {
        e.printStackTrace();
    return ResponseEntity.badRequest().body("History Of Products Bought could not Be Found");
        }
    }

    @PostMapping("purchase")
    public Object BuyItems(@RequestBody OrderDto.Checkout prodsBuy){
        try{
            System.out.println(prodsBuy);
            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = entityManager.getReference(User.class,userId);
            PastOrders history= OS.createOrder(prodsBuy,user);
            // fire and forget mail to customer
//            us.SendOrderReciept(history.getId(),false);
            return ResponseEntity.status(HttpStatus.CREATED).body(history);
            } catch (Exception e) {
            e.printStackTrace();
           return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
