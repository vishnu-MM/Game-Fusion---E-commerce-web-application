package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.Images;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImagesRepository extends JpaRepository<Images,Long> {
    Optional<Images> findByName(String name);
    Optional<Images> findById(Long id);
}
