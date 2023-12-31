package com.example.gamefusion.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Integer Id;
    @NotBlank(message = " First Name cannot be blank")
    private String firstName;
    private String lastName;
    @Size(min = 10, message = "Enter a valid phone number")
    @NotBlank(message = "Phone cannot be blank")
    private String phone;
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid Email ID")
    private String username;
    private String role;
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8 , message = "Password should be greater than 8 characters")
    private String password;
    private Boolean isActive;
    private UUID referralCode;
    private List<Integer> addressId;
}
