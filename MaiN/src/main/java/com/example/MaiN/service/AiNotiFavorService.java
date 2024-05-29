package com.example.MaiN.service;

import com.example.MaiN.dto.AiNotiDto;
import com.example.MaiN.dto.AiNotiFavorDto;
import com.example.MaiN.entity.AiNoti;
import com.example.MaiN.entity.AiNotiFavor;
import com.example.MaiN.entity.User;
import com.example.MaiN.repository.AiNotiFavorRepository;
import com.example.MaiN.repository.UserRepository;
import com.example.MaiN.repository.AiNotiRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    private UserRepository userRepository;

    @Transactional
    public AiNotiFavor addFavorite(AiNotiFavorDto aiNotiFavorDto) {
        User student = userRepository.findByNo(aiNotiFavorDto.getStudentNo())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + aiNotiFavorDto.getStudentNo()));

        AiNoti aiNoti = aiNotiRepository.findById(aiNotiFavorDto.getAiNotiId())
                .orElseThrow(() -> new RuntimeException("ai_noti not found with id: " + aiNotiFavorDto.getAiNotiId()));

        //이미 북마크로 추가했는지 확인
        boolean exists = aiNotiFavoritesRepository.existsByStudentNoAndAiNoti(student, aiNoti);
        if(exists) {
            throw new RuntimeException("This Favorite Already Exists");
        }

        AiNotiFavor favorite = new AiNotiFavor();
        favorite.setStudentNo(student); // 사용자 엔티티 설정
        favorite.setAiNoti(aiNoti); // ai_noti 엔티티 설정
        return aiNotiFavoritesRepository.save(favorite);
    }


    public void deleteFavorite(AiNotiFavorDto aiNotiFavorDto) {
        User student = userRepository.findByNo(aiNotiFavorDto.getStudentNo())
                .orElseThrow(() -> new RuntimeException("User not found"));
        aiNotiFavoritesRepository.findBystudentNoAndAiNotiId(student, aiNotiFavorDto.getAiNotiId())
                .ifPresent(aiNotiFavoritesRepository::delete);
    }

    public List<AiNotiDto> getAiNotiWithFavorites(String studentNo) {
        String jpql = "SELECT new com.example.MaiN.dto.AiNotiDto(an.id, an.title, an.link, an.date, CASE WHEN af IS NOT NULL THEN true ELSE false END) " +
                "FROM AiNoti an LEFT JOIN an.favoritesSet af " +
                "WITH af.studentNo.studentNo = :studentNo " +
                "ORDER BY an.date DESC";
        TypedQuery<AiNotiDto> query = entityManager.createQuery(jpql, AiNotiDto.class);
        query.setParameter("studentNo", studentNo);
        return query.getResultList();
    }
}