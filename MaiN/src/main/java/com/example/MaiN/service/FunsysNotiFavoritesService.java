package com.example.MaiN.service;

import com.example.MaiN.dto.FunsysNotiDto;
import com.example.MaiN.entity.funsys_noti;
import com.example.MaiN.entity.funsys_noti_favorites;
import com.example.MaiN.entity.users;
import com.example.MaiN.repository.funsys_noti_favorites_Repository;
import com.example.MaiN.repository.users_Repository;
import com.example.MaiN.repository.funsys_noti_Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FunsysNotiFavoritesService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private funsys_noti_favorites_Repository funsysNotiFavoritesRepository;

    @Autowired
    private funsys_noti_Repository funsysNotiRepository;

    @Autowired
    private users_Repository usersRepository;

    public funsys_noti_favorites addFavorite(String student_id, int funsysNotiId) {
        users student = usersRepository.findById(student_id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + student_id));

        funsys_noti funsysNoti = funsysNotiRepository.findById(funsysNotiId)
                .orElseThrow(() -> new RuntimeException("funsys_noti not found with id: " + funsysNotiId));

        funsys_noti_favorites favorite = new funsys_noti_favorites();
        favorite.setStudentId(student); // 사용자 엔티티 설정
        favorite.setFunsysNoti(funsysNoti); // funsys_noti 엔티티 설정
        return funsysNotiFavoritesRepository.save(favorite);
    }


    public void deleteFavorite(String studentId, int funsysNotiId) {
        users student = usersRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        funsysNotiFavoritesRepository.findByStudentIdAndFunsysNotiId(student, funsysNotiId)
                .ifPresent(funsysNotiFavoritesRepository::delete);
    }

    public List<FunsysNotiDto> getFunsysNotiWithFavorites(int studentId) {
        String jpql = "SELECT new com.example.MaiN.dto.FunsysNotiDto(an.id, an.title, an.link,  an.startDate, an.end_date, CASE WHEN af IS NOT NULL THEN true ELSE false END) " +
                "FROM funsys_noti an LEFT JOIN an.favoritesSet af " +
                "WITH af.studentId.student_id = :studentId " +
                "ORDER BY an.startDate DESC";
        TypedQuery<FunsysNotiDto> query = entityManager.createQuery(jpql, FunsysNotiDto.class);
        query.setParameter("studentId", studentId);
        return query.getResultList();
    }
}