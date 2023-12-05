package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    User findByUsername(String username);
    Boolean existsByUsername(String username);
    User findByReferralCode(UUID referralCode);
    Boolean existsByReferralCode(UUID referralCode);
    Page<User> findUsersByRole(String role, Pageable pageable);
    Page<User> searchAllByUsernameContainingIgnoreCase(String search, Pageable pageable);

    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :id")
    void updatePasswordById(Integer id, String password);

    @Query("SELECT u.isActive FROM User u WHERE u.id = :id")
    Boolean findIsActiveById(Integer id);

    @Modifying
    @Query("update User u set u.isActive = false where u.id = ?1")
    void blockUser( Integer id );

    @Modifying
    @Query("update User u set u.isActive = true where u.id = ?1")
    void unBlockUser( Integer id );
}
