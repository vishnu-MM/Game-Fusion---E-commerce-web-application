package com.example.gamefusion.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Table(name = "payment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name= "payment_id")
    private String paymentId;
    @Column(name= "amount")
    private Integer amount;
    @Column(name= "date")
    private Date date;
    @Column(name = "payment_method")
    private String paymentMethod;
    @Column(name = "status")
    private Boolean paymentStatus;
    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private OrderMain orderMain;
}
