package com.example.gamefusion.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandDto {

    private Long id;
    @NotNull(message = "Name should not be empty")
    private String name;
    @NotNull(message = "Status should not be empty")
    private boolean status;
    private Long logo;
}

