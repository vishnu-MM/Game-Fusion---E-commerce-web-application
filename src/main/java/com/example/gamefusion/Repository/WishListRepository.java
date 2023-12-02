package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.gamefusion.Entity.WishList;
import com.example.gamefusion.Entity.User;
import java.util.List;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Integer> {
    Boolean existsByUserAndProduct(User user, Product product);
    void deleteByUserAndProduct(User user, Product product);
    List<WishList> findByUser(User user);
    Boolean existsByUser(User user);
    void deleteByUser(User user);
}
