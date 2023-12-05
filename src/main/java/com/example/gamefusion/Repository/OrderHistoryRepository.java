package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.OrderMain;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.gamefusion.Entity.OrderHistory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory,Long> {
    List<OrderHistory> findByOrder(OrderMain order, Sort id);
    Boolean existsByOrder(OrderMain order);
}
