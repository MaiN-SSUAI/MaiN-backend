package com.example.MaiN.service;

import com.example.MaiN.dto.AiNotiDto;
import com.example.MaiN.model.ai_noti;
import com.example.MaiN.model.ai_noti_favorites;
import com.example.MaiN.model.users;
import com.example.MaiN.repository.ai_noti_favorites_Repository;
import com.example.MaiN.repository.users_Repository;
import com.example.MaiN.repository.ai_noti_Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AiNotiFavoritesService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ai_noti_favorites_Repository aiNotiFavoritesRepository;

    @Autowired
    private ai_noti_Repository aiNotiRepository;

    @Autowired
    private users_Repository usersRepository;

    public ai_noti_favorites addFavorite(String student_id, int aiNotiId) {
        users student = usersRepository.findById(student_id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + student_id));

        ai_noti aiNoti = aiNotiRepository.findById(aiNotiId)
                .orElseThrow(() -> new RuntimeException("ai_noti not found with id: " + aiNotiId));

        ai_noti_favorites favorite = new ai_noti_favorites();
        favorite.setStudentId(student); // 사용자 엔티티 설정
        favorite.setAiNoti(aiNoti); // ai_noti 엔티티 설정
        return aiNotiFavoritesRepository.save(favorite);
    }


    public void deleteFavorite(String studentId, int aiNotiId) {
        users student = usersRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        aiNotiFavoritesRepository.findByStudentIdAndAiNotiId(student, aiNotiId)
                .ifPresent(aiNotiFavoritesRepository::delete);
    }

    public List<AiNotiDto> getAiNotiWithFavorites(String studentId) {
        String jpql = "SELECT new com.example.MaiN.dto.AiNotiDto(an.id, an.title, an.link, an.date, CASE WHEN af IS NOT NULL THEN true ELSE false END) " +
                "FROM ai_noti an LEFT JOIN an.favoritesSet af " +
                "WITH af.studentId.student_id = :studentId " +
                "ORDER BY an.date DESC";
        TypedQuery<AiNotiDto> query = entityManager.createQuery(jpql, AiNotiDto.class);
        query.setParameter("studentId", studentId);
        return query.getResultList();
    }
}