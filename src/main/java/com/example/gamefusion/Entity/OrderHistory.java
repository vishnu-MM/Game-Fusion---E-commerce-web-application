package com.example.gamefusion.Entity;

import com.example.gamefusion.Configuration.UtilityClasses.OrderStatusUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Table(name = "order_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "order_id" , referencedColumnName = "id")
    private OrderMain order;
    @Column(name = "current_status")
    private String orderStatus;
    @Column(name = "date")
    private Date date;
}
