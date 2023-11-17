package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.BrandLogo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandLogoRepository extends JpaRepository<BrandLogo,Integer> {
    Optional<BrandLogo> findByName(String fileName);
}
