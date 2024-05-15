package com.example.MaiN.repository;

import com.example.MaiN.entity.Event;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservRepository extends CrudRepository<Event,Integer> {
    Event findByEventId(String eventId);
    @Query("SELECT e FROM Event e WHERE e.studentNo = :studentNo")
    List<Event> findBystudentNo(@Param("studentNo") String studentNo);

}
