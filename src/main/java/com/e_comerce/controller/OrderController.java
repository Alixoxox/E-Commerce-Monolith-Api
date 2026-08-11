package com.e_comerce.controller;
import java.util.List;
import com.e_comerce.DTO.OrderDto;
import com.e_comerce.model.OrderItems;
import com.e_comerce.model.PastOrders;
import com.e_comerce.service.OrderService;
import com.e_comerce.service.ProdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    ProdService PS;

    @GetMapping("history")
    public Object GetOrdersHistory(@RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size){
    try {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PageRequest pageable = PageRequest.of(page, size);
        Page<PastOrders> History= OS.GetHistory(userId,pageable);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(History);
    } catch (Exception e) {
    return ResponseEntity.badRequest().body(e);
    }
    }

    @GetMapping("history/bought/{HistoryNo}")
    public Object GetIndividualHistory(@PathVariable Long HistoryNo){
    try {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<OrderItems> History= OS.OrderHistoryProducts(userId,HistoryNo);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(History);
    } catch (Exception e) {
    return ResponseEntity.badRequest().body("History Of Products Bought could not Be Found");
    }
    }

    @PostMapping("purchase")
    public Object BuyItems(@RequestBody OrderDto.Checkout prodsBuy){
        try{
            PastOrders history= OS.createOrder(prodsBuy);
            return ResponseEntity.status(HttpStatus.CREATED).body(history);
        } catch (Exception e) {
           return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
