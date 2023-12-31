package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.Cart;
import com.example.gamefusion.Entity.Product;
import com.example.gamefusion.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    Page<Cart> findByUser(User user, Pageable pageable);
    List<Cart> findByUser(User user);
    void deleteByUser(User user);
    void deleteById(Integer id);
    Cart findByUserAndProduct(User user, Product product);
    Boolean existsByUserAndProduct(User user, Product product);
    @Query("SELECT c FROM Cart c " +
            "WHERE c.user = :user " +
            "AND c.product.status = :productStatus " +
            "AND c.product.qty > 0")
    List<Cart> findByUserAndProductStatusAndPositiveProductQty(User user, Boolean productStatus);
}
