package com.e_comerce.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.e_comerce.DTO.OrderDto;
import com.e_comerce.DTO.UserDto;
import com.e_comerce.model.Product;
import com.e_comerce.model.User;
import com.e_comerce.model.rating;
import com.e_comerce.repository.RatingRepo;
import com.e_comerce.repository.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

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

    public List<User> FetchUserData() {
        return UR.findAll();
    }

    public Object CreateUser(UserDto.Request UDR) {
        //instantiate new User
        try {
            User userM = new User();
            userM.setName(UDR.getName());
            userM.setEmail(UDR.getEmail());
            userM.setPassword(passwordEncoder.encode(UDR.getPassword()));
            userM.setCreated_at(LocalDateTime.now());
            User user = UR.save(userM);
            UserDto.UserSummaryDto summary = new UserDto.UserSummaryDto(user.getId(), user.getName(), user.getEmail());
            String token = jwtService.generateToken(userM.getEmail(),user.getId());
            return Map.of("Token",token,"UserData",summary);
        } catch (Exception e) {
            throw new RuntimeException("Email already exists.\nTry Another One!");
        }
    }
public Object LoginUser(UserDto.Login UDR) {

    User user = UR.findByEmail(UDR.getEmail())
            .orElseThrow(() -> new RuntimeException("Email not found"));

    boolean matches = passwordEncoder.matches(UDR.getPassword(), user.getPassword());

    if (!matches) {
        throw new RuntimeException("Password does not match. Try again.");
    }

    String token = jwtService.generateToken(user.getEmail(), user.getId());
    UserDto.UserSummaryDto summary = new UserDto.UserSummaryDto(user.getId(), user.getName(), user.getEmail());
    return Map.of("token", token, "userData", summary);
}
    @SneakyThrows
    public rating RateProduct(Long userId, Long productId, Integer stars){
        User user=entityManager.getReference(User.class,userId);
        Product prod=entityManager.getReference(Product.class,productId);
        rating rate= new rating(null, stars, null, LocalDateTime.now(),user,prod);
        return rp.save(rate);
    }

    @SneakyThrows
    public Double GetProdRating(Long productId){
        Double x= rp.getAvgRating(productId);
        return Math.round(x * 10) / 10.0; // 1 decimal place
    }

}
