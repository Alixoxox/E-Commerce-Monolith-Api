package com.e_comerce.controller;

import com.e_comerce.DTO.OrderDto;
import com.e_comerce.DTO.UserDto;
import com.e_comerce.DTO.productDTOs;
import com.e_comerce.model.PastOrders;
import com.e_comerce.model.enums.UserRole;
import com.e_comerce.service.AdminSevice;
import com.e_comerce.service.OrderService;
import com.e_comerce.service.ProdService;
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
    @Autowired
    private ProdService PS;

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
    @PostMapping("products/bulk")
    public Object BulkUploadProds(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(as.bulkInsertFromCsv(file));
        } catch (Exception e) {
            e.printStackTrace();
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
            US.SendOrderReciept(orderId,true);
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

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("product/create")
    public Object CreateProduct(@RequestPart("product") productDTOs.ProductDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image){
        try{
            if (dto.getDescription().length() > 254) throw new RuntimeException("For Now Description is limited to max length 255");
            PS.createProduct(dto,image);
            return ResponseEntity.status(HttpStatus.CREATED).body("Product "+dto.getTitle()+" Created");
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("product/edit/{productId}")
    public Object UpdateProduct(@RequestPart("product") productDTOs.ProductDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image){
        try{
            if (dto.getDescription().length() > 254) throw new RuntimeException("For Now Description is limited to max length 255");
            PS.updateProduct(dto,image);
            return ResponseEntity.ok("Product "+dto.getTitle()+"Edited");
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("product/del/{productId}")
    public Object DeleteProduct(@PathVariable Long productId){
        try{
            PS.deleteProduct(productId);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
