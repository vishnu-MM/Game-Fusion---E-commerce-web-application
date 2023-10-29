package com.example.gamefusion.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private Long id;
    @NotBlank(message = "Name should not be empty")
    private String name;
    @NotBlank(message = "Description should not be empty")
    private String description;
    @NotBlank(message = "Price should not be empty")
    private int price;
    @NotBlank(message = "Quantity should not be empty")
    private int qty;
    @NotBlank(message = "Brand should not be empty")
    private Long brandId;
    @NotBlank(message = "Category should not be empty")
    private Long categoryId;
}
