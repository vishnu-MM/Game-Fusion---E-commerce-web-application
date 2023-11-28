package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.OrderMain;
import com.example.gamefusion.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Integer> {
    Payment findByOrderMain(OrderMain orderMain);
    Boolean existsByOrderMain(OrderMain orderMain);
    @Query("select p.paymentStatus from Payment p where p.orderMain = ?1")
    Boolean findPaymentStatusByOrderMain(OrderMain orderMain);
}
