package com.example.MaiN.service;

import com.example.MaiN.dto.FunsysNotiDto;
import com.example.MaiN.entity.FunsysNoti;
import com.example.MaiN.entity.FunsysNotiFavor;
import com.example.MaiN.entity.User;
import com.example.MaiN.repository.FunsysNotiFavorRepository;
import com.example.MaiN.repository.UserRepository;
import com.example.MaiN.repository.FunsysNotiRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FunsysNotiFavorService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private FunsysNotiFavorRepository funsysNotiFavoritesRepository;

    @Autowired
    private FunsysNotiRepository funsysNotiRepository;

    @Autowired
    private UserRepository userRepository;

    public FunsysNotiFavor addFavorite(String studentNo, int funsysNotiId) {
        User student = userRepository.findByNo(studentNo)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + studentNo));

        FunsysNoti funsysNoti = funsysNotiRepository.findById(funsysNotiId)
                .orElseThrow(() -> new RuntimeException("funsysNoti not found with id: " + funsysNotiId));

        FunsysNotiFavor favorite = new FunsysNotiFavor();
        favorite.setStudentNo(student); // 사용자 엔티티 설정
        favorite.setFunsysNoti(funsysNoti); // funsys_noti 엔티티 설정
        return funsysNotiFavoritesRepository.save(favorite);
    }


    public void deleteFavorite(String studentNo, int funsysNotiId) {
        User student = userRepository.findByNo(studentNo)
                .orElseThrow(() -> new RuntimeException("User not found"));
        funsysNotiFavoritesRepository.findBystudentNoAndFunsysNotiId(student, funsysNotiId)
                .ifPresent(funsysNotiFavoritesRepository::delete);
    }

    public List<FunsysNotiDto> getFunsysNotiWithFavorites(int studentNo) {
        String jpql = "SELECT new com.example.MaiN.dto.FunsysNotiDto(an.id, an.title, an.link,  an.startDate, an.endDate, CASE WHEN af IS NOT NULL THEN true ELSE false END) " +
                "FROM FunsysNoti an LEFT JOIN an.favoritesSet af " +
                "WITH af.studentNo.studentNo = :studentNo " +
                "ORDER BY an.startDate DESC";
        TypedQuery<FunsysNotiDto> query = entityManager.createQuery(jpql, FunsysNotiDto.class);
        query.setParameter("studentNo", studentNo);
        return query.getResultList();
    }
}