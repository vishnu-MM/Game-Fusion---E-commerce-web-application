package com.example.gamefusion.Configuration.UtilityClasses;

import lombok.Getter;

@Getter
public enum PaymentMethodUtil {
    CASH_ON_DELIVERY(1),ONLINE_PAYMENT(2);
    private final Integer value;
    PaymentMethodUtil(Integer value) {
        this.value = value;
    }
}
