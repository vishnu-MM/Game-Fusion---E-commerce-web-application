package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findById(Long id);
    boolean existsById(Long id);

    @Query("SELECT c.name FROM Category c")
    List<String> findAllCategoryNames();

    @Query("SELECT c.status FROM Category c WHERE c.id = :id")
    Boolean findStatusById(Long id);

    @Modifying
    @Query("update Category c set c.status = false where c.id = ?1 or c.parentCategory.id = ?1")
    void setCategoryInactive(Long id);

    @Modifying
    @Query("update Category c set c.status = true where c.id = ?1 or c.parentCategory.id = ?1")
    void setCategoryActive(Long id);

    @Query("SELECT DISTINCT c.parentCategory.name FROM Category c WHERE c.parentCategory IS NOT NULL")
    List<String> findAllDistinctParentCategoryNames();

}
