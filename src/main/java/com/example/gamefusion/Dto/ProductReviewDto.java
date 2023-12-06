package com.example.gamefusion.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.sql.Date;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewDto {
    private Long id;
    @NotNull
    private Integer userId;
    @NotNull
    private Long productId;
    @NotNull
    private String review;
    @NotNull
    private Double rating;
    @NotNull
    private Date date;
}
