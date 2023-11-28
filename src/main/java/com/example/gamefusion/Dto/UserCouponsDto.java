package com.example.gamefusion.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCouponsDto {
    private Integer id;
    @NotNull
    private Integer userId;
    @NotNull
    private Integer couponId;
}
