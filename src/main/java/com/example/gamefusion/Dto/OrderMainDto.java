package com.example.gamefusion.Dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderMainDto {
    private Integer id;
    private String orderId;
    @NotNull
    @DateTimeFormat
    private Date date;
    @NotNull
    @PositiveOrZero
    private Integer amount;
    @NotNull
    private String paymentMethod;
    @NotNull
    private String status;
    @NotNull
    private Integer addressId;
    @NotNull
    private Integer userId;
}
