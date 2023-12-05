package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address,Integer> {
    boolean existsById(Integer id);
    boolean existsByUserId(Integer userId);
    List<Address> findByUserId(Integer userId);
    List<Address> findByUserIdAndStatus(Integer user_id, Boolean status);

    @Modifying
    @Query("update Address a set a.status = false where a.id = ?1")
    void blockAddress(Integer id);

    @Modifying
    @Query("update Address a set a.status = true where a.id = ?1")
    void unBlockAddress(Integer id);
}
