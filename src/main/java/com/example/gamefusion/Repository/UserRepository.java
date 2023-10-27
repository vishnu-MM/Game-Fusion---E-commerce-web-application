package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

//* Deference between CrudRepository & JpaRepository
@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    User findUserByUsername(String username);
    User findUserById(Integer id);
    Boolean existsByUsername(String username);
    @Modifying
    @Query("update User u set u.isActive = true where u.username = ?1")
    void unBlock( String username );
}
