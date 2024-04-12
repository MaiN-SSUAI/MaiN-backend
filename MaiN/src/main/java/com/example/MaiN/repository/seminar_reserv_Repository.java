package com.example.MaiN.repository;

import com.example.MaiN.entity.EventEntity;
import com.example.MaiN.entity.users;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface seminar_reserv_Repository extends CrudRepository<EventEntity,Integer> {
    EventEntity findByeventid(String eventid);
    @Query("SELECT e FROM EventEntity e WHERE e.student_id = :student_id")
    List<EventEntity> findByStudent_id(@Param("student_id") String student_id);

}
