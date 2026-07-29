package com.e_comerce.DTO;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

public class OrderDto {
    @Data
    @NoArgsConstructor
    public static class Item{
        private Long id;
        private Integer quantity;
    }
}
