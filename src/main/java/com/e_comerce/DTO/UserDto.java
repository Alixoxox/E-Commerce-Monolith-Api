package com.e_comerce.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.e_comerce.model.enums.Category;
import com.e_comerce.model.enums.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

public class UserDto {
    @Data
    public static class Request{
        private String name;
        private String password;
        private String email;
    }
    @Data
    public static class Login{
        private String password;
        private String email;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummaryDto{
        private Long id;
        private String name;
        private String email;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserOrderItemHist{
        private Long id;
        private String title;
        private String image;
        private Category category;
        private Integer quantity;
        private BigDecimal priceAtPurchase;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserOrderHist{
        private Long id;
        private LocalDateTime orderDate;
        private BigDecimal totalAmount;
        private String phoneNumber;
        private String city;
        private String country;
        private String postalCode;
        private String address;
        @Enumerated(EnumType.STRING)
        private OrderStatus status;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AllOrderHist {
    private Long id;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String phoneNumber;
    private String city;
    private String country;
    private String postalCode;
    private String address;
    private OrderStatus status;
    private String userName;
    private String email;
}
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class supportMsg {
        private String userEmail;
        private String subject;
        private String message;
        private  Boolean isHtml;
    }
    @Data
    @AllArgsConstructor
    public static class rateImg{
        private Long ratingId;
        private Long prodId;
        private UserDto.Attachment file;
        private String location;
    }
    @Data
    @AllArgsConstructor
    public static class Attachment{
        private String filename;
        private String contentType;
        private byte[] content;
    }
}
