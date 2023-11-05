package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand,Long> {
    Optional<Brand> findById(Long id);
}
