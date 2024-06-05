package com.example.MaiN.service;

import com.example.MaiN.dto.AiNotiDto;
import com.example.MaiN.dto.FunsysNotiDto;
import com.example.MaiN.dto.FunsysNotiFavorDto;
import com.example.MaiN.entity.FunsysNoti;
import com.example.MaiN.entity.FunsysNotiFavor;
import com.example.MaiN.entity.User;
import com.example.MaiN.repository.FunsysNotiFavorRepository;
import com.example.MaiN.repository.UserRepository;
import com.example.MaiN.repository.FunsysNotiRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public FunsysNotiFavor addFavorite(FunsysNotiFavorDto dto) {
        User student = userRepository.findByStudentNo(dto.getStudentNo())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getStudentNo()));

        FunsysNoti funsysNoti = funsysNotiRepository.findById(dto.getFunsysNotiId())
                .orElseThrow(() -> new RuntimeException("funsysNoti not found with id: " + dto.getFunsysNotiId()));

        boolean exists = funsysNotiFavoritesRepository.existsByStudentNoAndFunsysNoti(student, funsysNoti);
        if(exists) {
            throw new RuntimeException("This Favorite Already Exists");
        }

        FunsysNotiFavor favorite = new FunsysNotiFavor();
        favorite.setStudentNo(student); // 사용자 엔티티 설정
        favorite.setFunsysNoti(funsysNoti); // funsys_noti 엔티티 설정
        return funsysNotiFavoritesRepository.save(favorite);
    }


    public void deleteFavorite(FunsysNotiFavorDto dto) {
        User student = userRepository.findByStudentNo(dto.getStudentNo())
                .orElseThrow(() -> new RuntimeException("User not found"));
        funsysNotiFavoritesRepository.findBystudentNoAndFunsysNotiId(student, dto.getFunsysNotiId())
                .ifPresent(funsysNotiFavoritesRepository::delete);
    }

    public List<FunsysNotiDto> getFunsysNotiWithFavorites(int studentNo, int pageNo) {
        String jpql = "SELECT new com.example.MaiN.dto.FunsysNotiDto(an.id, an.title, an.link, an.startDate, an.endDate, CASE WHEN af.studentNo.studentNo IS NOT NULL THEN true ELSE false END) " +
                "FROM FunsysNoti an LEFT JOIN an.favoritesSet af ON af.studentNo.studentNo = :studentNo " +
                "ORDER BY an.startDate DESC";

        TypedQuery<FunsysNotiDto> query = entityManager.createQuery(jpql, FunsysNotiDto.class);
        query.setParameter("studentNo", studentNo);

        // 페이지 번호와 크기 설정
        Pageable pageable = PageRequest.of(pageNo - 1, 30, Sort.by(Sort.Direction.DESC, "date"));
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();

        query.setFirstResult(pageNumber * pageSize);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }
}