package com.example.gamefusion.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_sub")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private Integer qty;
    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private OrderMain orderMain;
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;
}
