package com.e_comerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class userController {

    @GetMapping
    public ResponseEntity<Object> GetAllUsers(){
        return ResponseEntity.ok("passed");
    }

}
