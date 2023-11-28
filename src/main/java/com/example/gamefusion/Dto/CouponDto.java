package com.example.gamefusion.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponDto {
    private Integer id;
    @NotNull
    private String couponCode;
    @NotNull
    private Double discount;
    @NotNull
    private Integer minimumAmount;
    @NotNull
    private Date expiryDate;
}
