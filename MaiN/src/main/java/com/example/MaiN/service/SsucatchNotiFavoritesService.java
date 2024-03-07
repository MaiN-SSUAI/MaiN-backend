package com.example.MaiN.service;

import com.example.MaiN.dto.SsucatchNotiDto;
import com.example.MaiN.model.ssucatch_noti;
import com.example.MaiN.model.ssucatch_noti_favorites;
import com.example.MaiN.model.users;
import com.example.MaiN.repository.ssucatch_noti_favorites_Repository;
import com.example.MaiN.repository.users_Repository;
import com.example.MaiN.repository.ssucatch_noti_Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SsucatchNotiFavoritesService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ssucatch_noti_favorites_Repository ssucatchNotiFavoritesRepository;

    @Autowired
    private ssucatch_noti_Repository ssucatchNotiRepository;

    @Autowired
    private users_Repository usersRepository;

    public ssucatch_noti_favorites addFavorite(String student_id, int ssucatchNotiId) {
        users student = usersRepository.findById(student_id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + student_id));

        ssucatch_noti ssucatchNoti = ssucatchNotiRepository.findById(ssucatchNotiId)
                .orElseThrow(() -> new RuntimeException("ssucatch_noti not found with id: " + ssucatchNotiId));

        ssucatch_noti_favorites favorite = new ssucatch_noti_favorites();
        favorite.setStudentId(student); // 사용자 엔티티 설정
        favorite.setSsucatchNoti(ssucatchNoti); // ssucatch 엔티티 설정
        return ssucatchNotiFavoritesRepository.save(favorite);
    }


    public void deleteFavorite(String studentId, int ssucatchNotiId) {
        users student = usersRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        ssucatchNotiFavoritesRepository.findByStudentIdAndSsucatchNotiId(student, ssucatchNotiId)
                .ifPresent(ssucatchNotiFavoritesRepository::delete);
    }

    public List<SsucatchNotiDto> getSsucatchNotiWithFavorites(String studentId) {
        String jpql = "SELECT new com.example.MaiN.dto.SsucatchNotiDto(an.id, an.title, an.link, an.progress, an.category, an.date, CASE WHEN af IS NOT NULL THEN true ELSE false END) " +
                "FROM ssucatch_noti an LEFT JOIN an.favoritesSet af " +
                "WITH af.studentId.student_id = :studentId " +
                "ORDER BY an.date DESC";
        TypedQuery<SsucatchNotiDto> query = entityManager.createQuery(jpql, SsucatchNotiDto.class);
        query.setParameter("studentId", studentId);
        return query.getResultList();
    }
}