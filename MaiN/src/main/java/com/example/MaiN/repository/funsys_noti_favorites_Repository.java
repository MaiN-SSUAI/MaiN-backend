package com.example.MaiN.repository;

import com.example.MaiN.entity.funsys_noti_favorites;
import com.example.MaiN.entity.users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface funsys_noti_favorites_Repository extends JpaRepository<funsys_noti_favorites, String> {

    Optional<funsys_noti_favorites> findByStudentIdAndFunsysNotiId(users student, int funsysNotiId);
}