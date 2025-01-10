package com.example.MaiN.repository;

import com.example.MaiN.entity.Reserv;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface ReservRepository extends CrudRepository<Reserv, Integer> {
    @Query("SELECT e FROM Reserv e WHERE e.userId = :userId")
    List<Reserv> findByUserId(@Param("userId") int userId);

    @Query("SELECT e FROM Reserv e WHERE e.id = :id")
    Reserv findByReservId(@Param("id") int reservId);

    Reserv findByEventId(String eventId);

    @Query("SELECT r FROM Reserv r WHERE r.startTime = :exactTime")
    List<Reserv> findReservationsStartingIn30Minutes(@Param("exactTime") LocalDateTime exactTime);

    @Query("SELECT r FROM Reserv r WHERE r.endTime = :exactTime")
    List<Reserv> findReservationsEndingIn5Minutes(@Param("exactTime") LocalDateTime exactTime);
}