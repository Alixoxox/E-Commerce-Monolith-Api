package com.e_comerce.controller;

import com.e_comerce.DTO.OrderDto;
import com.e_comerce.DTO.UserDto;
import com.e_comerce.model.PastOrders;
import com.e_comerce.model.enums.UserRole;
import com.e_comerce.service.AdminSevice;
import com.e_comerce.service.OrderService;
import com.e_comerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/")
public class adminController {

    @Autowired
    private UserService US;
    @Autowired
    private AdminSevice as;
    @Autowired
    private OrderService OS;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("all")
    public Object GetAllUsers(){
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(US.FetchUserData());
        }catch(Exception err){
            return ResponseEntity.badRequest();
        }
    }

    @PostMapping("auth/login")
    public Object loginUser(@RequestBody UserDto.Login UDLog){
        try{//here
            Object data = US.LoginUser(UDLog, UserRole.ADMIN);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(data);
        } catch (Exception e) {
            e.printStackTrace();
        return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("bulk")
    public Object BulkUploadProds(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(as.bulkInsertFromCsv(file));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(e.getMessage());
        }
    }
    @GetMapping("orders/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Object GetOrdersHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
    try {
        Page<UserDto.AllOrderHist> History= OS.GetAllOrders(page, size);
        return ResponseEntity.ok(History);
    } catch (Exception e) {
        e.printStackTrace();
    return ResponseEntity.badRequest().body(e);
    }
    }

    @PutMapping("orders/status/{orderId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Object UpdateOrderStatus(@PathVariable Long orderId, @RequestBody OrderDto.UpdateStatus body){
        try {
            PastOrders order = OS.UpdateStatus(orderId, body.getStatus());
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/users/count")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Object GetUserCount(){
        try{
            return ResponseEntity.ok(US.GetUserCount());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
