package com.e_comerce.DTO;

import java.math.BigDecimal;

import com.e_comerce.model.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class productDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductDto {
        private Long id;
        private String title;
        private String description;
        private Category category;
        private String image;
        private Integer stock;
        private BigDecimal price;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductSummary {
        private Long id;
        private String title;
        private BigDecimal price;
        private String description;
        private Category category;
        private String image;
        private Integer stock;
        private Double rate;
        private Long count;
    }
}
