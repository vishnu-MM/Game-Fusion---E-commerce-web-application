package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.Category;
import com.example.gamefusion.Entity.CategoryOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryOfferRepository extends JpaRepository<CategoryOffer, Integer> {
    CategoryOffer findByCategory(Category category);
    Boolean existsByCategory(Category category);
    void deleteByCategory(Category category);
}
