package com.e_comerce.service;

import static com.e_comerce.config.RedisAndRabbitConfig.EMAIL_QUEUE;
import static com.e_comerce.config.RedisAndRabbitConfig.IMAGE_DEL_QUEUE;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.e_comerce.DTO.OrderDto;
import com.e_comerce.DTO.UserDto;
import com.e_comerce.model.PastOrders;
import com.e_comerce.model.Product;
import com.e_comerce.model.User;
import com.e_comerce.model.enums.UserRole;
import com.e_comerce.model.rating;
import com.e_comerce.repository.PastOrderRepo;
import com.e_comerce.repository.RatingRepo;
import com.e_comerce.repository.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private S3Service s3Service;
    @Autowired
    private UserRepo UR;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private RatingRepo rp;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private buildRecieptHtml buildRecieptHtml;
    @Autowired
    private PastOrderRepo pastOrderRepo;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Cacheable("adminUsers")
    public List<User> FetchUserData() {
        return UR.findAll();
    }
    @Cacheable("UserCount")
    public long GetUserCount() {
        return UR.count();
    }
    @Caching(evict={@CacheEvict("UserCount"), @CacheEvict("adminUsers")})
    public Object CreateUser(UserDto.Request UDR,UserRole role) {
        if(role==null) throw new IllegalArgumentException("Role is Required");
        if (UDR.getEmail() == null || UDR.getEmail().isBlank()) throw new IllegalArgumentException("Email is required");
        try {
            User userM = new User();
            userM.setName(UDR.getName());
            userM.setEmail(UDR.getEmail());
            userM.setPassword(passwordEncoder.encode(UDR.getPassword()));
            userM.setUserRole(role);
            userM.setCreated_at(LocalDateTime.now());
            User user = UR.save(userM);
            UserDto.UserSummaryDto summary = new UserDto.UserSummaryDto(user.getId(), user.getName(), user.getEmail());
            String token = jwtService.generateToken(userM.getEmail(),user.getId(),role);
            return Map.of("Token",token,"UserData",summary);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Email already exists.\nTry Another One!");
        }
    }
    @SneakyThrows
    public Object LoginUser(UserDto.Login UDR, UserRole userRole) {
    if(userRole==null || UDR == null) throw new IllegalArgumentException("Login Mail/Pass and Role required");
    User user = UR.findByEmail(UDR.getEmail())
            .orElseThrow(() -> new RuntimeException("Email not found"));
    if(user.getUserRole() != userRole){
        throw new RuntimeException("You are not Authorized to access this Account");
    }
    boolean matches = passwordEncoder.matches(UDR.getPassword(), user.getPassword());

    if (!matches) {
        throw new RuntimeException("Password does not match. Try again.");
    }
    String token = jwtService.generateToken(user.getEmail(), user.getId(),userRole);
    UserDto.UserSummaryDto summary = new UserDto.UserSummaryDto(user.getId(), user.getName(), user.getEmail());
    return Map.of("Token", token, "UserData", summary);
}
    @SneakyThrows
    @Caching(evict = {
            @CacheEvict(value = "prodRating", key = "#productId"),
            @CacheEvict(value = "prodFeedback", key = "#productId")
    })
    public rating RateProduct(Long userId, Long productId, Integer stars, String Comment, String Url){
        if(userId==null || productId==null) throw new IllegalArgumentException("To Rate You have to be User and select Product");
        if(stars==null || stars > 5 || stars < 1) throw new IllegalArgumentException("You have to rate between 1-5");
        if (rp.existsByUserIdAndProductId(userId, productId)) {
        return null;
        }
        User user=entityManager.getReference(User.class,userId);
        Product prod=entityManager.getReference(Product.class,productId);
        rating rate= new rating(null,stars,Comment,LocalDateTime.now(),Url,user,prod);
        return rp.save(rate);
    }
    @SneakyThrows
    public void EditRateProduct(Long ratingId, Integer stars, String Comment, String Url){
        rating rate=entityManager.getReference(rating.class,ratingId);
        if(stars!=null){
            rate.setValue(stars);
        }if(Comment != null || !Comment.isBlank()){
            rate.setComment(Comment);
        }if(Url !=null || !Url.isBlank()){
            rate.setFeedbackImage(Url);
        }
        Long productId=rate.getProduct().getId();
        cacheManager.getCache("prodRating").evict(productId);
        cacheManager.getCache("prodFeedback").evict(productId);
        rp.save(rate);
    }

    @SneakyThrows
    @Cacheable(value="prodRating",key="#productId")
    public Double GetProdRating(Long productId){
        Double x= rp.getAvgRating(productId);
        return Math.round(x * 10) / 10.0; // 1 decimal place
    }
    @SneakyThrows
    @Cacheable("prodFeedback")
    public List<OrderDto.FeedbackDto> GetProductFeedback(Long productId) {
        return rp.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(r -> {
                    // 1. Evaluate the image URL and store it in a variable
                    String imageUrl = r.getFeedbackImage() != null ?
                            s3Service.getPresignedUrl(r.getFeedbackImage()) : null;

                    // 2. Return the DTO using the variable
                    return new OrderDto.FeedbackDto(r.getId(),r.getValue(), r.getComment(), r.getCreatedAt(), imageUrl,r.getUser().getName(),r.getUser().getId() );
                })
                .toList();
    }
    @SneakyThrows
    @Transactional
    public boolean RemoveRating(Long ratingId) {
        Optional<rating> x = rp.findById(ratingId);
        if (x.isPresent()) {
            rating rating = x.get();
            Long productId= rating.getProduct().getId();
            if (rating.getFeedbackImage() != null && rating.getFeedbackImage().startsWith("user-review/")) {
                rabbitTemplate.convertAndSend(IMAGE_DEL_QUEUE,rating.getFeedbackImage());
            }
            rp.delete(rating);
            cacheManager.getCache("prodRating").evict(productId);
            cacheManager.getCache("prodFeedback").evict(productId);
            return true;
        }
        return false;
    }

    @Transactional
    @SneakyThrows
    public void SendOrderReciept(Long historyId, Boolean statusChange) throws IOException {
      PastOrders order = pastOrderRepo.findById(historyId).orElseThrow();
      String html = buildRecieptHtml.build(order,statusChange);
      UserDto.supportMsg message=new UserDto.supportMsg(order.getUser().getEmail(), "Receipt - MEZN-" + order.getId(), html,true);
      rabbitTemplate.convertAndSend(EMAIL_QUEUE,message);
    }

}