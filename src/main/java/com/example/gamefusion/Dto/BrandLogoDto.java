package com.example.gamefusion.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandLogoDto {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String type;
    @NotNull
    private byte[] imageData;
}
