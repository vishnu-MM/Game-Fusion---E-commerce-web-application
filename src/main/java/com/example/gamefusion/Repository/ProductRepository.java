package com.example.gamefusion.Repository;

import java.util.List;
import com.example.gamefusion.Entity.Brand;
import org.springframework.data.domain.Page;
import com.example.gamefusion.Entity.Product;
import com.example.gamefusion.Entity.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p.status FROM Product p WHERE p.id = ?1")
    Boolean findStatusById(Long id);

    @Modifying
    @Query("update Product p set p.qty = :qty where p.id = :id")
    void updateQty(Long id, Integer qty);

    @Modifying
    @Query("update Product p set p.status = false where p.id = ?1")
    void blockProduct(Long id);

    @Modifying
    @Query("update Product p set p.status = true where p.id = ?1")
    void unBlockProduct(Long id);

    Integer countAllByBrand(Brand brand);
    Page<Product> findByStatus(boolean status, Pageable pageable);
    List<Product> searchAllByNameContainsIgnoreCase(String name);
    Page<Product> findByStatusAndCategoryStatus(boolean product_status,boolean Category_status, Pageable pageable);
    Page<Product> findByBrandAndStatusAndCategoryStatus(Brand brand, Boolean status, Boolean brandStatus,Pageable pageable);
    Page<Product> findByCategoryAndStatusAndCategoryStatus(Category category, Boolean status, Boolean categoryStatus,Pageable pageable);
}

