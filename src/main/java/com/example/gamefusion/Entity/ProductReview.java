package com.example.gamefusion.Entity;

import lombok.Data;
import java.sql.Date;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "product_review")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Column(name = "review_description")
    private String review;
    @Column(name = "rating")
    private Double rating;
    @Column(name = "date")
    private Date date;
}
