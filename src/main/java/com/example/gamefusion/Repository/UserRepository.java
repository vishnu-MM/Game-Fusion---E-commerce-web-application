package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    User findByUsername(String username);
//    User findUserByUsername(String username);
    Page<User> findUsersByRole(String role, Pageable pageable);
    Boolean existsByUsername(String username);
    boolean existsById(Integer id);

    @Query("SELECT u.isActive FROM User u WHERE u.id = :id")
    Boolean findIsActiveById(Integer id);

    @Modifying
    @Query("update User u set u.isActive = false where u.id = ?1")
    void blockUser( Integer id );

    @Modifying
    @Query("update User u set u.isActive = true where u.id = ?1")
    void unBlockUser( Integer id );
}
