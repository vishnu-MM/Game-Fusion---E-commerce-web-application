package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.Brand;
import com.example.gamefusion.Entity.Category;
import com.example.gamefusion.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p.status FROM Product p WHERE p.id = ?1")
    Boolean findStatusById(Long id);

    @Modifying
    @Query("update Product p set p.status = false where p.id = ?1")
    void blockProduct(Long id);

    @Modifying
    @Query("update Product p set p.status = true where p.id = ?1")
    void unBlockProduct(Long id);

    Page<Product> findByStatus(boolean status, Pageable pageable);
    Page<Product> findByCategoryAndStatusAndCategoryStatus(Category category, Boolean status, Boolean categoryStatus,
                                                           Pageable pageable);
    Page<Product> findByBrandAndStatusAndCategoryStatus(Brand brand, Boolean status, Boolean brandStatus,
                                                        Pageable pageable);
    Page<Product> findByStatusAndCategoryStatus(boolean product_status,boolean Category_status, Pageable pageable);
}

