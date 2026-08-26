package com.e_comerce.service;
import com.e_comerce.DTO.UserDto;
import com.e_comerce.model.Product;
import com.e_comerce.model.User;
import com.e_comerce.model.enums.UserRole;
import com.e_comerce.model.rating;
import com.e_comerce.repository.RatingRepo;
import com.e_comerce.repository.UserRepo;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.e_comerce.config.RedisAndRabbitConfig.IMAGE_DEL_QUEUE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepo userRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JWTService jwtService;
    @Mock
    private EntityManager entityManager;
    @Mock
    private RatingRepo ratingRepo;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private CacheManager cacheManager;
    private final String dummyEmail = "sufyan@gmail.com";
    private final String dummyPass ="123456";

    @Test
    void notCreateUserWithoutRole(){
        User user=new User();
        user.setId(1L);
        UserDto.Request dtoUser=new UserDto.Request();
        assertThrows(IllegalArgumentException.class,
            () -> userService.CreateUser(dtoUser,null));
        verifyNoInteractions(userRepo);
    }
    @Test
    void notCreateUserWithoutMail() {
    UserDto.Request dto = new UserDto.Request();
    assertThrows(
            IllegalArgumentException.class,
            () -> userService.CreateUser(dto,UserRole.USER));
    verifyNoInteractions(userRepo);
    }
    @Test
    void createUserWithPropperCreds(){
     User user= new User();
     UserDto.Request dto =new UserDto.Request();

     dto.setEmail(dummyEmail);
     dto.setPassword(dummyPass);

     user.setId(1L);
     user.setEmail(dto.getEmail());

     when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
     when(userRepo.save(any(User.class))).thenReturn(user);
     when(jwtService.generateToken(anyString(),anyLong(),eq(UserRole.USER))).thenReturn("token");
     userService.CreateUser(dto,UserRole.USER);
     verify(passwordEncoder).encode(anyString());
     verify(userRepo).save(any(User.class));
     verify(jwtService).generateToken(eq(user.getEmail()), eq(1L), eq(UserRole.USER));
    }

    @Test
    void loginWithoutRoleParam(){
        UserDto.Login input = new UserDto.Login();
        assertThrows(
            IllegalArgumentException.class,
            () -> userService.LoginUser(input,null));
        verifyNoInteractions(userRepo);
    }
    @Test
    void loginUserWithProperCreds() {
    UserDto.Login dto = new UserDto.Login();
    dto.setEmail(dummyEmail);
    dto.setPassword(dummyPass);

    User user = new User();
    user.setId(1L);
    user.setEmail(dummyEmail);
    String encodedPass= passwordEncoder.encode("123456");
    user.setPassword(encodedPass);
    user.setUserRole(UserRole.USER);

    when(userRepo.findByEmail(dummyEmail)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(eq(dto.getPassword()), eq(user.getPassword()))).thenReturn(true);
    when(jwtService.generateToken(dummyEmail,1L,UserRole.USER)).thenReturn("token");

    Object result = userService.LoginUser(dto, UserRole.USER);

    assertNotNull(result);

    verify(userRepo).findByEmail(dummyEmail);
    verify(passwordEncoder).matches(eq(dto.getPassword()),eq(user.getPassword()));
    verify(jwtService).generateToken(dummyEmail,1L,UserRole.USER);
}
    @Test
    void loginUserWithAdminRoleParam(){
        UserDto.Login dto = new UserDto.Login();
        dto.setEmail(dummyEmail);
        dto.setPassword(dummyPass);

        User user = new User();
        user.setId(1L);
        user.setEmail(dummyEmail);
        user.setPassword(passwordEncoder.encode(dummyPass));
        user.setUserRole(UserRole.USER);

        when(userRepo.findByEmail(dummyEmail)).thenReturn(Optional.of(user));
        assertThrows(
            RuntimeException.class,
            () -> userService.LoginUser(dto,UserRole.ADMIN));
    }

    @Test
    void rejectLoginWithDiffPassword(){
        UserDto.Login dto = new UserDto.Login();
        dto.setEmail(dummyEmail);
        dto.setPassword(dummyPass);

        User user = new User();
        user.setId(1L);
        user.setEmail(dummyEmail);
        user.setPassword(passwordEncoder.encode(dummyPass));
        user.setUserRole(UserRole.USER);

        when(userRepo.findByEmail(dummyEmail)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(eq(dto.getPassword()), eq(user.getPassword()))).thenReturn(true);

        assertThrows(
                RuntimeException.class,
                () -> userService.LoginUser(dto,UserRole.USER));
    }

    @Test
    void userRatesProdRating(){
        when(entityManager.getReference(eq(User.class),anyLong())).thenReturn(new User());
        when(entityManager.getReference(eq(Product.class),anyLong())).thenReturn(new Product());
        userService.RateProduct(1L,1L,3,null,null);
        verify(entityManager).getReference(User.class,1L);
        verify(entityManager).getReference(Product.class,1L);
        verify(ratingRepo).save(any(rating.class));
    }
    @Test
    void noUserRatesProduct(){
        assertThrows(IllegalArgumentException.class,
                ()->  userService.RateProduct(null,1L,3,null,null));
    }
    @Test
    void noUserNorProductRating(){
        assertThrows(IllegalArgumentException.class,
                ()-> userService.RateProduct(null,null,null,null,null));    }

@Test
void removeRatingWithAWSImage() {
    Cache ratingCache = mock(Cache.class);
    Cache feedbackCache = mock(Cache.class);
    Product product = new Product();
    product.setId(1L);

    rating rating = new rating();
    rating.setId(1L);
    rating.setProduct(product);
    rating.setFeedbackImage("user-review/image.jpg");

    when(ratingRepo.findById(1L)).thenReturn(Optional.of(rating));
    when(cacheManager.getCache("prodRating")).thenReturn(ratingCache);

    when(cacheManager.getCache("prodFeedback")).thenReturn(feedbackCache);
    Boolean result = userService.RemoveRating(1L);

    assertTrue(result);

    verify(rabbitTemplate).convertAndSend(IMAGE_DEL_QUEUE, "user-review/image.jpg");
    verify(ratingRepo).delete(rating);
    verify(ratingCache).evict(product.getId());
    verify(feedbackCache).evict(product.getId());
    }

    @Test
    void removeRatingNotFound() {
        when(ratingRepo.findById(1L)).thenReturn(Optional.empty());
        Boolean result = userService.RemoveRating(1L);

        assertFalse(result);

        verify(ratingRepo, never()).delete(any(rating.class));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString());
    }
}