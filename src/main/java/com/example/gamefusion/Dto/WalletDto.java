package com.example.gamefusion.Dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.sql.Date;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletDto {
    private Long id;
    @NotNull
    private Integer userId;
    @NotNull
    private Date dateTime;
    @NotNull
    private String transactionType;
    @NotNull
    @PositiveOrZero
    private Double amount;
    @NotNull
    private String description;
}
