package com.e_comerce.controller;

import java.util.List;

import com.e_comerce.DTO.UserDto;
import com.e_comerce.model.User;
import com.e_comerce.model.wishlist;
import com.e_comerce.service.UserService;
import com.e_comerce.service.WishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/")
public class userController {

    @Autowired
    private UserService US;
    @Autowired
    private WishService ws;

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
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("auth/login")
    public Object loginUser(@RequestBody UserDto.Login UDLog){
        try{
            User userM = US.LoginUser(UDLog);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(userM);
        } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("mark/wishlist/{userId}/{productId}")
    public void MarkWishlist(@PathVariable Long userId,@PathVariable Long productId){
        try {
            ws.MarkWish(userId, productId);
            ResponseEntity.status(HttpStatus.CREATED).body("Product Marked");
        }catch (Exception e){
            ResponseEntity.badRequest().body("Something went wrong while Marking.\nPlease Try Again Later");
        }
    }
    // TODO :
    // Implement streaming service when price go down then alert people for message
    @GetMapping("fetch/wishlist/{userId}")
    public void MarkWishlist(@PathVariable Long userId){
        try {
            List<wishlist> wishes= ws.GetWishes(userId);
            ResponseEntity.status(HttpStatus.ACCEPTED).body(wishes);
        }catch (Exception e){
            ResponseEntity.badRequest().body("Something went wrong while Marking.\nPlease Try Again Later");
        }
        }

}
