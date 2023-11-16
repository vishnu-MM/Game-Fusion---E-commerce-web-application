package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.OrderMain;
import com.example.gamefusion.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderMainRepository extends JpaRepository<OrderMain,Integer> {
    Page<OrderMain> findByUser(Pageable pageable, User user);
}
