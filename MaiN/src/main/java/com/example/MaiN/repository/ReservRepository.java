package com.example.MaiN.repository;

import com.example.MaiN.entity.Event;
import org.springframework.data.repository.CrudRepository;

public interface ReservRepository extends CrudRepository<Event,Integer> {
    Event findByEventId(String eventId);
}
