package com.e_comerce.model.enums;

import jakarta.persistence.Embeddable;

@Embeddable
public enum OrderStatus {
    PENDING, SHIPPED, DELIVERED, CANCELLED
}
