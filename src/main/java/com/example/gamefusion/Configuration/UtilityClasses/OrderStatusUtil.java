package com.example.gamefusion.Configuration.UtilityClasses;

import jakarta.persistence.EntityNotFoundException;

public enum OrderStatusUtil {
    PENDING(1),
    SHIPPED(2),
    DELIVERED(3),
    CANCELED(4),
    RETURNED(5),
    REQUEST_CANCEL(6),
    REQUEST_REPLACE(7);
    private final Integer value;
    OrderStatusUtil(Integer value) {
        this.value = value;
    }

    public static String getOrderStatusByValue(Integer value) {
        for (OrderStatusUtil method : OrderStatusUtil.values()) {
            if (method.value.equals(value)) {
                return method.toString();
            }
        }
        throw new EntityNotFoundException("Valid Payment Method is Not found");
    }
}
