package com.e_comerce.controller;
import java.util.List;
import java.util.Optional;
import com.e_comerce.model.Product;
import com.e_comerce.service.ProdService;
import com.e_comerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
        @RequestParam(defaultValue = "10") int size) {
        try {
            return ResponseEntity.ok(PS.getProducts(page,size));
        }catch(Exception err){
            return ResponseEntity.badRequest().body(err.getMessage());
        }   
    }

    @GetMapping("{productId}")
    public Object GetOneProduct(@PathVariable Long productId){
        try{
            Optional<Product> prod= PS.getOneProd(productId);
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
    //related prods
    @GetMapping("category/{category}")
    public Object GetCategoryProduct(@PathVariable String category){
        try{
            System.out.println(category);
            List<Product> prod= PS.ProdsByCategory(category);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(prod);
        }catch(Exception e){
            System.out.println(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("fetch/rating/{productId}")
    public Object FetchProductRating(@PathVariable Long productId){
        try{
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(us.GetProdRating(productId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("ratings/{productId}")
    public Object FetchProductFeedback(@PathVariable Long productId){
        try{
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(us.GetProductFeedback(productId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //everyone's ratings comutative and comment section wrt product
}
