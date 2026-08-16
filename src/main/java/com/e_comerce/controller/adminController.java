package com.e_comerce.controller;

import com.e_comerce.DTO.UserDto;
import com.e_comerce.model.enums.UserRole;
import com.e_comerce.service.AdminSevice;
import com.e_comerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("all")
    public Object GetAllUsers(){
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(US.FetchUserData());
        }catch(Exception err){
            return ResponseEntity.badRequest();
        }
    }

    // add a seeding script for admin which runs once on startup

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

}
