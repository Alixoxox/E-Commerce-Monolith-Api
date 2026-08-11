package com.e_comerce.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.e_comerce.model.Product;
import com.e_comerce.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class OrderDto {
    @Data
    @NoArgsConstructor
    public static class Item{
        private Long id;
        private Integer quantity;
    }
    @Data
    @NoArgsConstructor
    public static class RateProd{
        private Long productId;
        private Integer rating;
        private String Comment;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FeedbackDto {
        private Long id;
        private Integer value;
        private String comment;
        private LocalDateTime createdAt;
        private String feedbackImage;
        private String userName;
        private Long userId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Checkout{
        private String phoneNumber;
        private String address;
        private String city;
        private String country;
        private String postalCode;
        private List<Item> products;
    }
}
