package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.User;
import org.springframework.data.domain.Page;
import com.example.gamefusion.Entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import com.example.gamefusion.Entity.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview,Long> {
    Page<ProductReview> findByProduct(Product product, Pageable pageable);
    Page<ProductReview> findByUser(User user, Pageable pageable);
    List<ProductReview> findByProduct(Product product);
    List<ProductReview> findByUser(User user);
    Boolean existsByProduct(Product product);
    Boolean existsByUser(User user);
}
