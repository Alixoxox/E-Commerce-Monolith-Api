package com.e_comerce.controller;

import java.util.List;
import java.util.Optional;

import com.e_comerce.model.Product;
import com.e_comerce.model.enums.category;
import com.e_comerce.service.ProdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product/")
public class productController {

    @Autowired
    private ProdService PS;

    @GetMapping("all")
    public Object GetAllProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        try {
            return ResponseEntity.ok(PS.getProducts(page,size));
        }catch(Exception err){
            return ResponseEntity.badRequest().body(err.getMessage());
        }
    }

    @GetMapping("{id}")
    public Object GetOneProduct(@PathVariable Long id){
        try{
            Optional<Product> prod= PS.getOneProd(id);
            return ResponseEntity.accepted().body(prod);
        }catch (Exception err){
            return ResponseEntity.badRequest().body(err.getMessage());
        }
    }

    @GetMapping("categories")
    public Object GetCategories(){
        try{
            List<String> x= PS.getCategories();
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(x);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("category/{category}")
    public Object GetCategoryProduct(@PathVariable category cat){
        try{
            List<Product> prod= PS.ProdsByCategory(cat);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(prod);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
