package com.e_comerce.controller;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import com.e_comerce.DTO.OrderDto;
import com.e_comerce.model.OrderItems;
import com.e_comerce.model.PastOrders;
import com.e_comerce.repository.PastOrderRepo;
import com.e_comerce.service.OrderService;
import com.e_comerce.service.ProdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders/")
public class OrderController {

    @Autowired
    OrderService OS;

    @Autowired
    ProdService PS;

    @GetMapping("history/{userId}")
    public Object GetOrdersHistory(@PathVariable Long userId,@RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size){
    try {
        PageRequest pageable = PageRequest.of(page, size);
        Page<PastOrders> History= OS.GetHistory(userId,pageable);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(History);
    } catch (Exception e) {
    return ResponseEntity.badRequest().body(e);
    }
    }

    @GetMapping("history/bought/{HistoryNo}/{userId}")
    public Object GetIndividualHistory(@PathVariable Long userId,@PathVariable Long HistoryNo){
    try {
        List<OrderItems> History= OS.OrderHistoryProducts(userId,HistoryNo);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(History);
    } catch (Exception e) {
    return ResponseEntity.badRequest().body("History Of Products Bought could not Be Found");
    }
    }

    @PostMapping("purchase/{userId}")
    public Object BuyItems(@PathVariable Long userId,@RequestBody List<OrderDto.Item> prodsBuy){
        try{
            // 1 stores the final to the pastOrders
            PastOrders history= OS.createOrder(prodsBuy, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
