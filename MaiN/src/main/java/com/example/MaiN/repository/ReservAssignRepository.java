package com.example.MaiN.repository;
import com.example.MaiN.entity.EventAssign;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ReservAssignRepository extends JpaRepository<EventAssign, Integer> {

}
