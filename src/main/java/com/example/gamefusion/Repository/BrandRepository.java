package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand,Long> {
    boolean existsByName(String name);
    Page<Brand> searchBrandByNameContainingIgnoreCase(String name, Pageable pageable);
    
    @Query("SELECT b.status FROM Brand b WHERE b.id = ?1")
    boolean findStatusById(Long id);
    @Modifying
    @Query("update Brand b set b.status = false where b.id = ?1")
    void activate(Long id);
    @Modifying
    @Query("update Brand b set b.status = true where b.id = ?1")
    void inActivate(Long id);
}
