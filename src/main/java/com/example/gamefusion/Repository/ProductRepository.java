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

    Page<Product> findByStatusAndCategoryStatusAndBrandStatus(
        Boolean status, Boolean category_status, Boolean brand_status, Pageable pageable
    );

    //* PRODUCT SEARCH
    List<Product> searchAllByNameContainsIgnoreCase(String name);
    Page<Product> searchAllByNameContainsIgnoreCaseAndStatusAndCategoryStatusAndBrandStatus(
        String name, Boolean status, Boolean categoryStatus, Boolean brandStatus, Pageable pageable
    );
    Page<Product> searchAllByCategoryNameContainsIgnoreCaseAndStatusAndCategoryStatusAndBrandStatus(
        String categoryName, Boolean status, Boolean categoryStatus, Boolean brandStatus, Pageable pageable
    );
    Page<Product> searchAllByBrandNameContainsIgnoreCaseAndStatusAndCategoryStatusAndBrandStatus(
        String brand_name, Boolean status, Boolean category_status, Boolean brand_status, Pageable pageable
    );

    //* FILTER PRODUCTS BY CATEGORY
    Integer countAllByCategoryAndBrandStatusAndCategoryStatusAndStatus(
        Category category, Boolean brandStatus, Boolean categoryStatus, Boolean status
    );
    Page<Product> findByCategoryAndBrandStatusAndCategoryStatusAndStatus(
        Category category, Boolean status, Boolean categoryStatus, Boolean brandStatus, Pageable pageable
    );

    //* FILTER PRODUCTS BY CATEGORY AND PRICE BETWEEN MIN & MAX
    Integer countAllByBrandAndBrandStatusAndCategoryStatusAndStatusAndPriceBetween(
        Brand brand, Boolean brandStatus, Boolean categoryStatus, Boolean status, int minPrice, int maxPrice
    );
    Page<Product> findByBrandAndBrandStatusAndCategoryStatusAndStatusAndPriceBetween(
        Brand brand, Boolean brandStatus, Boolean categoryStatus, Boolean status, int minPrice, int maxPrice, Pageable pageable
    );

    //* FILTER PRODUCTS BY BRAND
    Integer countAllByBrandAndBrandStatusAndCategoryStatusAndStatus(
        Brand brand, Boolean brand_status, Boolean category_status, Boolean status
    );
    Page<Product> findByBrandAndBrandStatusAndCategoryStatusAndStatus(
        Brand brand, Boolean brand_status, Boolean category_status, Boolean status, Pageable pageable
    );

    //* FILTER PRODUCTS BY BRAND AND PRICE BETWEEN MIN & MAX
    Integer countAllByCategoryAndBrandStatusAndCategoryStatusAndStatusAndPriceBetween(
        Category category, Boolean brandStatus, Boolean categoryStatus, Boolean status, int minPrice, int maxPrice
    );
    Page<Product> findByCategoryAndBrandStatusAndCategoryStatusAndStatusAndPriceBetween(
        Category category, Boolean brandStatus, Boolean categoryStatus, Boolean status, int minPrice, int maxPrice, Pageable pageable
    );
}

