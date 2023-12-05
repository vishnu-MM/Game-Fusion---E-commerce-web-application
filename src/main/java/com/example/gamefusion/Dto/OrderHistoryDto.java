package com.example.gamefusion.Dto;

import com.example.gamefusion.Configuration.UtilityClasses.OrderStatusUtil;
import com.example.gamefusion.Entity.OrderMain;
import jakarta.persistence.*;
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