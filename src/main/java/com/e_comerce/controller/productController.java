package com.e_comerce.controller;

import java.util.Arrays;
import java.util.List;

import com.e_comerce.DTO.OrderDto;
import com.e_comerce.DTO.productDTOs;
import com.e_comerce.model.enums.Category;
import com.e_comerce.service.ProdService;
import com.e_comerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product/")
public class productController {

    @Autowired
    private ProdService PS;
    @Autowired
    private UserService us;

    @GetMapping("all")
    public Object GetAllProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "") String search,
        @RequestParam(defaultValue = "") Category category,
        @RequestParam(defaultValue = "") String sort) {
        try {
           Page<productDTOs.ProductSummary> x= PS.getProducts(page,size, search, category, sort);
            return ResponseEntity.ok(x);
        }catch(Exception err){
            err.printStackTrace();
            return ResponseEntity.badRequest().body(err.getMessage());
        }   
    }

    @GetMapping("{productId}")
    public Object GetOneProduct(@PathVariable Long productId){
        try{
            return ResponseEntity.accepted().body(PS.getOneProd(productId));
        }catch (Exception err){
            err.printStackTrace();
            return ResponseEntity.badRequest().body(err.getMessage());
        }
    }

    //related prods
    @GetMapping("category/{category}")
    public Object GetCategoryProduct(@PathVariable Category category){
        try{
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(PS.ProdsByCategory(category));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("fetch/rating/{productId}")
    public Object FetchProductRating(@PathVariable Long productId){
        try{
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(us.GetProdRating(productId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("ratings/{productId}")
    public Object FetchProductFeedback(@PathVariable Long productId){
        try{
            List<OrderDto.FeedbackDto> x= us.GetProductFeedback(productId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(x);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
