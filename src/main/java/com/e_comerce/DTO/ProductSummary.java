package com.e_comerce.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSummary {
    private Long id;
    private String title;
    private BigDecimal price;
    private String description;
    private String category;
    private String image;
    private Integer stock;
    private Double rate;
    private Long count;
}
