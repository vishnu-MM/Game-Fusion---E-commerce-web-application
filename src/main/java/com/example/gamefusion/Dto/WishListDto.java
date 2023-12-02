package com.example.gamefusion.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishListDto {
    private Integer id;
    @NotNull
    private Integer userId;
    @NotNull
    private Long productId;
}
