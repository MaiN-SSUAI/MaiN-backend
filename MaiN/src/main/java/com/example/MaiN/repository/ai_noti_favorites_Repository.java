package com.example.MaiN.repository;

import com.example.MaiN.model.ai_noti_favorites;
import com.example.MaiN.model.users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ai_noti_favorites_Repository extends JpaRepository<ai_noti_favorites, String> {

    Optional<ai_noti_favorites> findByStudentIdAndAiNotiId(users student, int aiNotiId);
}