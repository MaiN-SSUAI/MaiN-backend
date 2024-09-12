package com.example.MaiN.repository;

//import com.example.MaiN.entity.Event;
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

    @Query("SELECT r FROM Reserv r WHERE FUNCTION('STR_TO_DATE', r.startTime, '%Y-%m-%d %H:%i:%s') BETWEEN :now AND :in30Minutes")
    List<Reserv> findReservationsBetween(@Param("now") LocalDateTime now, @Param("in30Minutes") LocalDateTime in30Minutes);

    @Query("SELECT r FROM Reserv r WHERE FUNCTION('STR_TO_DATE', r.endTime, '%Y-%m-%d %H:%i:%s') BETWEEN :now AND :in5Minutes")
    List<Reserv> findReservationsEndingIn5Minutes(@Param("now") LocalDateTime now, @Param("in5Minutes") LocalDateTime in5Minutes);
}