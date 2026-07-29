package com.e_comerce.controller;

import com.e_comerce.DTO.UserDto;
import com.e_comerce.model.User;
import com.e_comerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/")
public class userController {

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
    @PostMapping("auth/register")
    public Object CreateUser(@RequestBody UserDto.Request UR){
        try{
            User UDR = US.CreateUser(UR);
            return ResponseEntity.status(HttpStatus.CREATED).body(UDR);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @PostMapping("auth/login")
    public Object loginUser(@RequestBody UserDto.Login UDLog){
        try{
            User userM = US.LoginUser(UDLog);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(userM);
        } catch (Exception e) {
        return ResponseEntity.badRequest().body(e);
        }
    }



}
