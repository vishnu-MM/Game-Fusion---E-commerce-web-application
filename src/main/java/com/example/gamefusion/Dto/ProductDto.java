package com.example.gamefusion.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private Long id;
    @NotBlank(message = "Name should not be empty")
    private String name;
    @NotBlank(message = "Description should not be empty")
    private String description;
    @NotNull(message = "Price should not be empty")
    @Positive(message = "Price must be a positive number")
    private int price;
    @NotNull(message = "Quantity should not be empty")
    @Positive(message = "Quantity must be a positive number")
    private int qty;
    @NotNull(message = "Brand should not be empty")
    private Long brandId;
    @NotNull(message = "Category should not be empty")
    private Long categoryId;
    private Boolean status;
    private List<Long> imageIds;
}
