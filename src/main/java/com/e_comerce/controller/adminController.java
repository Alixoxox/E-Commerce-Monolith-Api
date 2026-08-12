package com.e_comerce.controller;

import com.e_comerce.model.enums.OrderStatus;
import com.e_comerce.service.AdminSevice;
import com.e_comerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/")
public class adminController {

    @Autowired
    private UserService US;
    @Autowired
    private AdminSevice as;

    @GetMapping("all")
    public Object GetAllUsers(){
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(US.FetchUserData());
        }catch(Exception err){
            return ResponseEntity.badRequest();
        }
    }

    @PostMapping("bulk")
    public Object BulkUploadProds(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(as.bulkInsertFromCsv(file));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(e.getMessage());
        }
    }
    @PostMapping("change/{status}")
    public Object ChangeOrderStatus(@PathVariable("status") OrderStatus status,@PathVariable("OrderId") Long OrderId){
        try {
            as.changeOrderStatus(status,OrderId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Successfully changed status");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(e.getMessage());
        }
    }

}
