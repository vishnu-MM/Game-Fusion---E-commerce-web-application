package com.example.gamefusion.Configuration.UtilityClasses;

import com.example.gamefusion.Configuration.ExceptionHandlerConfig.EntityNotFound;
import lombok.Getter;

@Getter
public enum PaymentMethodUtil {
    CASH_ON_DELIVERY(1),
    ONLINE_PAYMENT(2),
    WALLET_PAYMENT(3);
    private final Integer value;
    PaymentMethodUtil(Integer value) {
        this.value = value;
    }

    public static String getPaymentMethodByValue(Integer value) {
        for (PaymentMethodUtil method : PaymentMethodUtil.values()) {
            if (method.value.equals(value)) {
                return method.toString();
            }
        }
        throw new EntityNotFound("Valid Payment Method is Not found");
    }
    public Boolean isValidPaymentMethod(Integer value) {
        for (PaymentMethodUtil method : PaymentMethodUtil.values()) {
            if (method.value.equals(value)) return true;
        }
        return false;
    }
}
