package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.OrderMain;
import com.example.gamefusion.Entity.OrderSub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderSubRepository extends JpaRepository<OrderSub, Integer> {
    List<OrderSub> findByOrderMain(OrderMain orderMain);
}
