package com.example.gamefusion.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    private Long id;
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @NotNull(message = "status cannot be blank")
    private Boolean status;
    private Long parentId;
}
