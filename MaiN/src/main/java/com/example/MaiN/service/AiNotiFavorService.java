package com.example.MaiN.service;

import com.example.MaiN.dto.AiNotiDto;
import com.example.MaiN.entity.AiNoti;
import com.example.MaiN.entity.AiNotiFavor;
import com.example.MaiN.entity.Users;
import com.example.MaiN.repository.AiNotiFavorRepository;
import com.example.MaiN.repository.UsersRepository;
import com.example.MaiN.repository.AiNotiRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AiNotiFavorService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AiNotiFavorRepository aiNotiFavoritesRepository;

    @Autowired
    private AiNotiRepository aiNotiRepository;

    @Autowired
    private UsersRepository usersRepository;

    public AiNotiFavor addFavorite(String studentId, int aiNotiId) {
        Users student = usersRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + studentId));

        AiNoti aiNoti = aiNotiRepository.findById(aiNotiId)
                .orElseThrow(() -> new RuntimeException("ai_noti not found with id: " + aiNotiId));

        AiNotiFavor favorite = new AiNotiFavor();
        favorite.setStudentId(student); // 사용자 엔티티 설정
        favorite.setAiNoti(aiNoti); // ai_noti 엔티티 설정
        return aiNotiFavoritesRepository.save(favorite);
    }


    public void deleteFavorite(String studentId, int aiNotiId) {
        Users student = usersRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        aiNotiFavoritesRepository.findByStudentIdAndAiNotiId(student, aiNotiId)
                .ifPresent(aiNotiFavoritesRepository::delete);
    }

    public List<AiNotiDto> getAiNotiWithFavorites(String studentId) {
        String jpql = "SELECT new com.example.MaiN.dto.AiNotiDto(an.id, an.title, an.link, an.date, CASE WHEN af IS NOT NULL THEN true ELSE false END) " +
                "FROM AiNoti an LEFT JOIN an.favoritesSet af " +
                "WITH af.studentId.studentId = :studentId " +
                "ORDER BY an.date DESC";
        TypedQuery<AiNotiDto> query = entityManager.createQuery(jpql, AiNotiDto.class);
        query.setParameter("studentId", studentId);
        return query.getResultList();
    }
}