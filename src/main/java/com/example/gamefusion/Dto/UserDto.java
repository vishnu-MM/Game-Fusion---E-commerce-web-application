package com.example.gamefusion.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NotBlank(message = "Name cannot be blank")
    private String firstName;
    private String lastName;
    @NotBlank(message = "Phone cannot be blank ")
    private String phone;
    @NotBlank(message = "Email cannot be blank ")
    @Email(message = "Invalid Email ID")
    private String username;
    private String role;
    @NotBlank
    @Size(min = 8 , message = "Password should be greater than 8 characters")
    private String password;
}
