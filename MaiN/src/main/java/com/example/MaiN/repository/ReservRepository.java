package com.example.MaiN.repository;

import com.example.MaiN.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservRepository extends CrudRepository<Event,Integer> {
    Event findByEventId(String eventId);

    List<Event> findByStudentId(@Param("studentId") String studentId);

}
