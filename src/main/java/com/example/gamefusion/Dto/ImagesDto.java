package com.example.gamefusion.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImagesDto {

    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String type;
    @NotNull
    private String filePath;
    @NotNull
    private Long productId;
}
