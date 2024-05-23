package com.example.MaiN.repository;
import com.example.MaiN.entity.Event;
import com.example.MaiN.entity.EventAssign;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservAssignRepository extends JpaRepository<EventAssign, Integer> {
    EventAssign findByEventId(String eventId);
    List<EventAssign> findByReservId(int reservation_id);
}
