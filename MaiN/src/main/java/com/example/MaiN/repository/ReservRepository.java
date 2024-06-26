package com.example.MaiN.repository;

//import com.example.MaiN.entity.Event;
import com.example.MaiN.entity.EventAssign;
import com.example.MaiN.entity.Reserv;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface ReservRepository extends CrudRepository<Reserv, Integer> {
    @Query("SELECT e FROM Reserv e WHERE e.userId = :userId")
    List<Reserv> findByUserId(@Param("userId") int userId);

    @Query("SELECT e FROM Reserv e WHERE e.id = :id")
    Reserv findByReservId(@Param("id") int reservId);

    Reserv findByEventId(String eventId);
}