package com.example.gamefusion.Dto;

import com.example.gamefusion.Entity.OrderMain;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
    private Integer amount;
    @NotNull
    private Date date;
    @NotNull
    private String paymentMethod;
    private Boolean paymentStatus;
    @NotNull
    private Integer orderId;
}
