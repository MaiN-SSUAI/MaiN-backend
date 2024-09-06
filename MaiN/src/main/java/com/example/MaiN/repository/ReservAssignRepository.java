package com.example.MaiN.repository;
import com.example.MaiN.entity.Reserv;
import com.example.MaiN.entity.ReservAssign;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservAssignRepository extends JpaRepository<ReservAssign, Integer> {
//    ReservAssign findByEventId(String eventId);
    List<ReservAssign> findByReservId(int reservation_id);

    List<ReservAssign> findByUserId(@Param("userId") int userId);

    List<Integer> findUserIdsByReservId(int reservId);

    void deleteByReservAndUserId(Reserv reserv, Integer userId);
}
