package com.example.gamefusion.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String username;
    @Column(name = "type")
    private String role;
    @Column(name = "password")
    private String password;
    @Column( name = "active_status")
    private Boolean isActive;


}
