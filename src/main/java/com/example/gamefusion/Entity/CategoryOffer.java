package com.example.gamefusion.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Table(name = "category_offer")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "minimum_amount")
    private Integer minimumAmount;
    @Column(name = "discount")
    private Double discount;
    @Column(name = "expiry_date")
    private Date expiryDate;
    @OneToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
}
