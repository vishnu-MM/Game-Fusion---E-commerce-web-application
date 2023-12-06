package com.example.gamefusion.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

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
    @Column( name = "referral_id")
    private UUID referralCode;

    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Address> addressList;

}
