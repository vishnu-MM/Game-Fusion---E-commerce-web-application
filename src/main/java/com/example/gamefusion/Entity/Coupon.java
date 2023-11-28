package com.example.gamefusion.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Table(name = "coupon")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "coupon_code")
    private String couponCode;
    @Column(name = "discount")
    private Double discount;
    @Column(name = "minimum_amount")
    private Integer minimumAmount;
    @Column(name = "expiry_date")
    private Date expiryDate;
}
