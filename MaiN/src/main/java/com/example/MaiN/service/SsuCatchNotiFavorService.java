package com.example.MaiN.service;

import com.example.MaiN.dto.SsuCatchNotiDto;
import com.example.MaiN.entity.SsuCatchNoti;
import com.example.MaiN.entity.SsuCatchNotiFavor;
import com.example.MaiN.entity.Users;
import com.example.MaiN.repository.SsuCatchNotiFavorRepository;
import com.example.MaiN.repository.UsersRepository;
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
    private UsersRepository usersRepository;

    public SsuCatchNotiFavor addFavorite(String studentId, int ssuCatchNotiId) {
        Users student = usersRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + studentId));

        SsuCatchNoti ssuCatchNoti = ssuCatchNotiRepository.findById(ssuCatchNotiId)
                .orElseThrow(() -> new RuntimeException("SsuCatchNoti not found with id: " + ssuCatchNotiId));

        SsuCatchNotiFavor favorite = new SsuCatchNotiFavor();
        favorite.setStudentId(student); // 사용자 엔티티 설정
        favorite.setSsuCatchNoti(ssuCatchNoti); // ssucatch 엔티티 설정
        return ssuCatchNotiFavoritesRepository.save(favorite);
    }


    public void deleteFavorite(String studentId, int ssuCatchNotiId) {
        Users student = usersRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        ssuCatchNotiFavoritesRepository.findByStudentIdAndSsuCatchNotiId(student, ssuCatchNotiId)
                .ifPresent(ssuCatchNotiFavoritesRepository::delete);
    }

    public List<SsuCatchNotiDto> getSsuCatchNotiWithFavorites(String studentId) {
        String jpql = "SELECT new com.example.MaiN.dto.SsuCatchNotiDto(an.id, an.title, an.link, an.progress, an.category, an.date, CASE WHEN af IS NOT NULL THEN true ELSE false END) " +
                "FROM SsuCatchNoti an LEFT JOIN an.favoritesSet af " +
                "WITH af.studentId.studentId = :studentId " +
                "ORDER BY an.date DESC";
        TypedQuery<SsuCatchNotiDto> query = entityManager.createQuery(jpql, SsuCatchNotiDto.class);
        query.setParameter("studentId", studentId);
        return query.getResultList();
    }
}