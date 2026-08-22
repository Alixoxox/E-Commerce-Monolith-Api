package com.e_comerce.controller;

import static com.e_comerce.config.RedisAndRabbitConfig.*;

import java.util.List;

import com.e_comerce.DTO.OrderDto;
import com.e_comerce.DTO.UserDto;
import com.e_comerce.model.enums.UserRole;
import com.e_comerce.model.rating;
import com.e_comerce.model.wishlist;
import com.e_comerce.repository.RatingRepo;
import com.e_comerce.service.EmailService;
import com.e_comerce.service.S3Service;
import com.e_comerce.service.UserService;
import com.e_comerce.service.WishService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users/")
public class userController {

    @Autowired
    private UserService US;
    @Autowired
    private WishService ws;
    @Autowired
    private S3Service ss;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("auth/register")
    public Object CreateUser(@RequestBody UserDto.Request UR){
        try{
            Object UDR = US.CreateUser(UR,UserRole.USER);
            return ResponseEntity.status(HttpStatus.CREATED).body(UDR);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("auth/login")
    public Object loginUser(@RequestBody UserDto.Login UDLog){
        try{
            Object data = US.LoginUser(UDLog, UserRole.USER);
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
    // TODO : Implement streaming service when price go down then alert people for message -> OPTIONAL

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

    @PostMapping("rate")
    public Object RateProduct(
            @RequestPart("rating") OrderDto.RateProd rp,
            @RequestPart(value = "image", required = false) MultipartFile image){
        try{
            System.out.println(rp);
            System.out.println(image);
            if (rp.getComment().isEmpty() && rp.getComment().length() > 255) throw new RuntimeException("For Now Description is limited to max length 255");
            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            rating rate= US.RateProduct(userId, rp.getProductId(),rp.getRating(),rp.getComment(),null);
            System.out.println(rate);
            if(image!=null && !image.isEmpty()){
                UserDto.Attachment newImg = new UserDto.Attachment(image.getOriginalFilename(), image.getContentType(), image.getBytes());
                UserDto.rateImg r= new UserDto.rateImg(rate.getId(), rate.getProduct().getId(), newImg,"user-review/");
                rabbitTemplate.convertAndSend(IMAGE_QUEUE,r);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Your Response Has been Recorded");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Autowired
    private RatingRepo ratingRepo;

    @PutMapping("complete/rating/{ratingId}")
    public Object CompleteProductRate(
            @RequestPart("rating") OrderDto.EditRateProd rp,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @PathVariable Long ratingId ){
        try{
            if (rp.getRating() == null || rp.getComment() == null || rp.getComment().isBlank() || image == null || image.isEmpty()) {
             throw new RuntimeException("There Should be atLeast One Change");
            }
            if (rp.getComment()!=null && rp.getComment().length() > 255) throw new RuntimeException("For Now Description is limited to max length 255");

            if(image != null && !image.isEmpty()){
                rating rate= ratingRepo.findById(ratingId).orElseThrow(() -> new RuntimeException("Feedback not found"));
                UserDto.Attachment newImg = new UserDto.Attachment(image.getOriginalFilename(), image.getContentType(), image.getBytes());
                UserDto.rateImg r= new UserDto.rateImg(ratingId, rate.getProduct().getId(), newImg,"user-review/");
                if(rate.getFeedbackImage()!=null && rate.getFeedbackImage().startsWith("user-review/")){
                    rabbitTemplate.convertAndSend(IMAGE_DEL_QUEUE,rate.getFeedbackImage()); // delete prev image
                }
                rabbitTemplate.convertAndSend(IMAGE_QUEUE,r); // store newer image
            }
            US.EditRateProduct(ratingId,rp.getRating(),rp.getComment(),null);
            return ResponseEntity.status(HttpStatus.CREATED).body("The Product has now been altered");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("remove/{ratingId}")
    public Object DeleteProductRating(@PathVariable Long ratingId){
        try{
            boolean x=US.RemoveRating(ratingId);
            String s;
            if(x){
                s= "Removed from Db";
            }else{
                s="Failed to remove from Db.\nTry Again Later";
            }
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(s);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("sendMail")
    public Object SendMail(@RequestBody UserDto.supportMsg sm){
        try{
            sm.setIsHtml(false);
            rabbitTemplate.convertAndSend(EMAIL_QUEUE,sm);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("We have received your message and will hear from us soon.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}