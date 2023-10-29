package com.example.gamefusion.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandDto {

    private Long id;
    @NotBlank(message = "Name should not be empty")
    private String name;
    @NotBlank(message = "Status should not be empty")
    private boolean status;
    private byte[] logo;
}

