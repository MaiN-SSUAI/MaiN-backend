package com.example.MaiN.repository;

import com.example.MaiN.entity.FunsysNoti;
import com.example.MaiN.entity.SsuCatchNoti;
import com.example.MaiN.entity.SsuCatchNotiFavor;
import com.example.MaiN.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SsuCatchNotiFavorRepository extends JpaRepository<SsuCatchNotiFavor, String> {

    Optional<SsuCatchNotiFavor> findBystudentNoAndSsuCatchNotiId(User student, int ssuCatchNotiId);
    boolean existsByStudentNoAndSsuCatchNoti(User student, SsuCatchNoti ssuCatchNoti);

}