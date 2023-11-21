package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.OrderMain;
import com.example.gamefusion.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Integer> {
    Payment findByOrderMain(OrderMain orderMain);
    Boolean existsByOrderMain(OrderMain orderMain);
}
