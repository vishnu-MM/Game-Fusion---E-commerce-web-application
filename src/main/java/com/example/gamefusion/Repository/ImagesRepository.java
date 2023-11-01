package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.Images;
import com.example.gamefusion.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImagesRepository extends JpaRepository<Images,Long> {
    Optional<Images> findByName(String name);
    List<Images> findByProduct(Product product);
}
