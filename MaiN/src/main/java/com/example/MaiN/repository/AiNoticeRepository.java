package com.example.MaiN.repository;

import com.example.MaiN.dto.AiNoticeDto;
import com.example.MaiN.entity.AiNotice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface AiNoticeRepository extends PagingAndSortingRepository<AiNotice, Integer>, JpaRepository<AiNotice, Integer> {

    @Query("SELECT new com.example.MaiN.dto.AiNoticeDto(a.id, a.title, a.link, a.date, " +
            "CASE WHEN nf.id IS NOT NULL THEN true ELSE false END) " +
            "FROM AiNotice a LEFT JOIN NoticeFavorite nf ON a.id = nf.noticeId " +
            "AND nf.user.studentNo = :studentNo AND nf.noticeType = 'ai' " +
            "ORDER BY a.date DESC")
    Page<AiNoticeDto> findAllWithFavoriteStatus(String studentNo, Pageable pageable);
}
