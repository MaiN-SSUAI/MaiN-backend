package com.example.MaiN.repository;

import com.example.MaiN.entity.FunsysNotiFavor;
import com.example.MaiN.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FunsysNotiFavorRepository extends JpaRepository<FunsysNotiFavor, String> {

    Optional<FunsysNotiFavor> findByStudentIdAndFunsysNotiId(Users student, int funsysNotiId);
}