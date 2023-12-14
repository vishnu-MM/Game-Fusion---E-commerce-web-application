package com.example.gamefusion.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private Integer id;
    private String paymentId;
    @NotNull
    private Double amount;
    @NotNull
    private Date date;
    @NotNull
    private String paymentMethod;
    private Boolean paymentStatus;
    @NotNull
    private Integer orderId;
}
