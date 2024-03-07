package com.example.MaiN.repository;

import com.example.MaiN.model.ssucatch_noti_favorites;
import com.example.MaiN.model.users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ssucatch_noti_favorites_Repository extends JpaRepository<ssucatch_noti_favorites, String> {

    Optional<ssucatch_noti_favorites> findByStudentIdAndSsucatchNotiId(users student, int ssucatchNotiId);
}