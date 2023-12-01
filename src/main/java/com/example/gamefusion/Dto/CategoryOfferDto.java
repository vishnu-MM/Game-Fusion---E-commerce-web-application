package com.example.gamefusion.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryOfferDto {
    private Long id;
    @NotNull
    private Integer minimumAmount;
    @NotNull
    private Double discount;
    @NotNull
    private Date expiryDate;
    @NotNull
    private Long categoryId;
}
