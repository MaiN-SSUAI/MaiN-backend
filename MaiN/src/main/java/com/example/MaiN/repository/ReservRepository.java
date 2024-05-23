package com.example.MaiN.repository;

import com.example.MaiN.entity.Event;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservRepository extends CrudRepository<Event,Integer> {
    @Query("SELECT e FROM Event e WHERE e.userId = :userId")
    List<Event> findByUserId(@Param("userId") int userId);

}
