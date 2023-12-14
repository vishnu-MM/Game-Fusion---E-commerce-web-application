package com.example.gamefusion.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistoryDto {

    private Long id;
    @NotNull
    private Integer orderId;
    @NotNull
    private String orderStatus;
    @NotNull
    private Date date;
}