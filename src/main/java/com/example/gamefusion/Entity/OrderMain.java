package com.example.gamefusion.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@Entity
@Table(name = "order_main")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderMain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "order_id")
    private String orderId;
    @Column(name = "date")
    private Date date;
    @Column(name = "amount")
    private Integer amount;
    @Column(name = "status")
    private String status;
    @Column(name = "payment_method")
    private String paymentMethod;
    @Column(name = "country", nullable = false, length = 100)
    private String country;
    @Column(name = "state", nullable = false, length = 100)
    private String state;
    @Column(name = "district", nullable = false, length = 100)
    private String district;
    @Column(name = "street_address", nullable = false, length = 150)
    private String streetAddress;
    @Column(name = "pin_code", nullable = false)
    private Integer pinCode;
    @Column(name = "phone", precision = 12, scale = 0)
    private Long phone;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
