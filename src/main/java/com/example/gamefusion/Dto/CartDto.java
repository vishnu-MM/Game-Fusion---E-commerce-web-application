package com.example.gamefusion.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {

    private Integer id;
    @NotNull
    private Integer qty;
    @NotNull
    private Long productID;
    @NotNull
    private Integer userID;
}
