package com.e_comerce.controller;

import com.e_comerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/")
public class adminController {

    @Autowired
    private UserService US;

    @GetMapping("all")
    public Object GetAllUsers(){
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(US.FetchUserData());
        }catch(Exception err){
            return ResponseEntity.badRequest();
        }
    }
}
