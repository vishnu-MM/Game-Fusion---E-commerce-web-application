package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.OrderMain;
import com.example.gamefusion.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.sql.Date;
import java.util.List;

@Repository
public interface OrderMainRepository extends JpaRepository<OrderMain,Integer> {

    Boolean existsByUser(User user);
    Integer countAllByDate(Date date);
    List<OrderMain> findByUser(User user);
    Integer countAllByStatus(String status);
    Page<OrderMain> findByDate(Pageable pageable, Date date);
    Page<OrderMain> findByUser(Pageable pageable, User user);
    Page<OrderMain> findByStatus(String status, Pageable pageable);
    Page<OrderMain> findByDateAndStatus(Date date, String status, Pageable pageable);
    Page<OrderMain> findByDateBetween(Date startDate, Date endDate, Pageable pageable);
    Page<OrderMain> findByDateBetweenAndStatus(Date startDate, Date endDate, String status, Pageable pageable);

    @Query("SELECT COUNT(o) FROM OrderMain o where year(o.date) = ?1 and month(o.date) = ?2")
    Integer countAllByDateYearAndDateMonth(int year, int month);
}
