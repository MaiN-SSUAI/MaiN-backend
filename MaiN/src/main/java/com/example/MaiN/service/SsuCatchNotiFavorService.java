package com.example.MaiN.service;

import com.example.MaiN.dto.SsuCatchNotiDto;
import com.example.MaiN.dto.SsuCatchNotiFavorDto;
import com.example.MaiN.entity.SsuCatchNoti;
import com.example.MaiN.entity.SsuCatchNotiFavor;
import com.example.MaiN.entity.User;
import com.example.MaiN.repository.SsuCatchNotiFavorRepository;
import com.example.MaiN.repository.UserRepository;
import com.example.MaiN.repository.SsuCatchNotiRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SsuCatchNotiFavorService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SsuCatchNotiFavorRepository ssuCatchNotiFavoritesRepository;

    @Autowired
    private SsuCatchNotiRepository ssuCatchNotiRepository;

    @Autowired
    private UserRepository userRepository;

    public SsuCatchNotiFavor addFavorite(SsuCatchNotiFavorDto dto) {
        User student = userRepository.findByNo(dto.getStudentNo())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getStudentNo()));

        SsuCatchNoti ssuCatchNoti = ssuCatchNotiRepository.findById(dto.getSsuCatchNotiId())
                .orElseThrow(() -> new RuntimeException("SsuCatchNoti not found with id: " + dto.getSsuCatchNotiId()));

        boolean exists = ssuCatchNotiFavoritesRepository.existsByStudentNoAndSsuCatchNoti(student, ssuCatchNoti);
        if(exists) {
            throw new RuntimeException("This Favorite Already Exists");
        }

        SsuCatchNotiFavor favorite = new SsuCatchNotiFavor();
        favorite.setStudentNo(student); // 사용자 엔티티 설정
        favorite.setSsuCatchNoti(ssuCatchNoti); // ssucatch 엔티티 설정

        return ssuCatchNotiFavoritesRepository.save(favorite);
    }


    public void deleteFavorite(SsuCatchNotiFavorDto ssuCatchNotiFavorDto) {
        User student = userRepository.findByNo(ssuCatchNotiFavorDto.getStudentNo())
                .orElseThrow(() -> new RuntimeException("User not found"));
        ssuCatchNotiFavoritesRepository.findBystudentNoAndSsuCatchNotiId(student, ssuCatchNotiFavorDto.getSsuCatchNotiId())
                .ifPresent(ssuCatchNotiFavoritesRepository::delete);
    }

    public List<SsuCatchNotiDto> getSsuCatchNotiWithFavorites(String studentNo) {
        String jpql = "SELECT new com.example.MaiN.dto.SsuCatchNotiDto(an.id, an.title, an.link, an.progress, an.category, an.date, CASE WHEN af IS NOT NULL THEN true ELSE false END) " +
                "FROM SsuCatchNoti an LEFT JOIN an.favoritesSet af " +
                "WITH af.studentNo.studentNo = :studentNo " +
                "ORDER BY an.date DESC";
        TypedQuery<SsuCatchNotiDto> query = entityManager.createQuery(jpql, SsuCatchNotiDto.class);
        query.setParameter("studentNo", studentNo);
        return query.getResultList();
    }
}