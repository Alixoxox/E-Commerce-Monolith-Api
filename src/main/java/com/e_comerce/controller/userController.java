package com.e_comerce.controller;

import java.util.List;

import com.e_comerce.DTO.UserDto;
import com.e_comerce.model.wishlist;
import com.e_comerce.service.UserService;
import com.e_comerce.service.WishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/")
public class userController {

    @Autowired
    private UserService US;
    @Autowired
    private WishService ws;

    @PostMapping("auth/register")
    public Object CreateUser(@RequestBody UserDto.Request UR){
        try{
            Object UDR = US.CreateUser(UR);
            return ResponseEntity.status(HttpStatus.CREATED).body(UDR);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("auth/login")
    public Object loginUser(@RequestBody UserDto.Login UDLog){
        try{
            Object data = US.LoginUser(UDLog);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(data);
        } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("mark/wishlist/{productId}")
    public Object MarkWishlist(@PathVariable Long productId){
        try {
            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            ws.MarkWish(userId, productId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Product Marked");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Something went wrong while Marking.\nPlease Try Again Later");
        }
    }
    // TODO : Implement streaming service when price go down then alert people for message

    @GetMapping("watch/wishlist")
    public Object FetchWishlist(){
        try {
            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            List<wishlist> wishes= ws.GetWishes(userId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(wishes);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Something went wrong while Marking.\nPlease Try Again Later");
        }
    }
    // TODO : Allow images to store on S3 bucket -> comment and allow to put product images
    @PostMapping("rate/{productId}/{stars}")
    public Object RateProduct(@PathVariable Long productId, Integer stars){
        try{
            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            return ResponseEntity.status(HttpStatus.CREATED).body(US.RateProduct(userId, productId,stars));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
