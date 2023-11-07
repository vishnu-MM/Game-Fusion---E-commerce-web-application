package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address,Integer> {
    List<Address> findByUserId(Integer userId);
    Optional<Address> findById(Integer id);
    boolean existsById(Integer id);
    boolean existsByUserId(Integer userId);

}
