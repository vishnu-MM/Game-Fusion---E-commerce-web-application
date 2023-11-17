package com.example.gamefusion.Dto;

import jakarta.validation.constraints.*;
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
    private Integer userId;
    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country should not exceed 100 characters")
    private String country;
    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State should not exceed 100 characters")
    private String state;
    @NotBlank(message = "District is required")
    @Size(max = 100, message = "District should not exceed 100 characters")
    private String district;
    @NotBlank(message = "Street address is required")
    @Size(max = 150, message = "Street address should not exceed 150 characters")
    private String streetAddress;
    @NotNull(message = "Pin code is required")
    @Min(value = 100000, message = "Pin code should be at least 6 digits")
    @Max(value = 999999, message = "Pin code should be at most 6 digits")
    private Integer pinCode;
    private String phone;
}
