package com.example.gamefusion.Configuration.UtilityClasses;

import java.security.SecureRandom;

public class CouponCodeGenerator {
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 8;
    public static String generate() {
        StringBuilder code = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < LENGTH; i++) {
            code.append(
                ALPHA_NUMERIC_STRING.charAt(
                    random.nextInt(
                        ALPHA_NUMERIC_STRING.length()
                    )
                )
            );
        }
        return code.toString();
    }
}
