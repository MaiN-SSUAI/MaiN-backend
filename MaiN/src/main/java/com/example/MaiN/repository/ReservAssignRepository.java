package com.example.MaiN.repository;
import com.example.MaiN.entity.Reserv;
import com.example.MaiN.entity.ReservAssign;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservAssignRepository extends JpaRepository<ReservAssign, Integer> {

    List<ReservAssign> findByUserId(int userId);

    @Modifying
    @Query("DELETE FROM ReservAssign ra WHERE ra.reserv = :reserv AND ra.userId = :userId")
    void deleteByReservAndUserId(@Param("reserv") Reserv reserv, @Param("userId") int userId);

//    List<Integer> findUserIdsByReserv(Reserv reserv);
    @Query("SELECT ra.userId FROM ReservAssign ra WHERE ra.reserv.id = :reservId")
    List<Integer> findUserIdsByReservId(@Param("reservId") int reservId);
}
