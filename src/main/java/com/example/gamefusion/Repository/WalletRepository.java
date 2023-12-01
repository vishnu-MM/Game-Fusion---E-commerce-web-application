package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.User;
import com.example.gamefusion.Entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    boolean existsByUser(User user);
    List<Wallet> findByUser(User user);
}

