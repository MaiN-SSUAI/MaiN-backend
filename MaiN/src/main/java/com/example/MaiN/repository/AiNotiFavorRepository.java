package com.example.MaiN.repository;

import com.example.MaiN.entity.AiNotiFavor;
import com.example.MaiN.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiNotiFavorRepository extends JpaRepository<AiNotiFavor, String> {

    Optional<AiNotiFavor> findByStudentIdAndAiNotiId(Users student, int aiNotiId);
}