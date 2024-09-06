package com.example.MaiN.repository;
import com.example.MaiN.entity.Reserv;
import com.example.MaiN.entity.ReservAssign;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservAssignRepository extends JpaRepository<ReservAssign, Integer> {

    List<ReservAssign> findByUserId(int userId);

    void deleteByReservAndUserId(Reserv reserv, Integer userId);

    List<Integer> findUserIdsByReserv(Reserv reserv);
}
