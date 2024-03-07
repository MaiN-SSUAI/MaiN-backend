package com.example.MaiN.repository;

import com.example.MaiN.entity.EventEntity;
import org.springframework.data.repository.CrudRepository;

public interface seminar_reserv_Repository extends CrudRepository<EventEntity,Integer> {
    EventEntity findByeventid(String eventid);
}
