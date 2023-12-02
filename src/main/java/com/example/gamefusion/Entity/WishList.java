package com.example.gamefusion.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wishlist")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;
}
