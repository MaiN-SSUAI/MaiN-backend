package com.example.MaiN.repository;

import com.example.MaiN.dto.AiNoticeDto;
import com.example.MaiN.dto.SsucatchNoticeDto;
import com.example.MaiN.entity.SsucatchNotice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface SsucatchNoticeRepository extends PagingAndSortingRepository<SsucatchNotice, Integer>, JpaRepository<SsucatchNotice, Integer> {

    @Query("SELECT new com.example.MaiN.dto.SsucatchNoticeDto(s.id, s.title, s.link, s.progress, s.category, s.date, " +
            "CASE WHEN nf.id IS NOT NULL THEN true ELSE false END) " +
            "FROM SsucatchNotice s LEFT JOIN NoticeFavorite nf ON s.id = nf.noticeId " +
            "AND nf.user.studentNo = :studentNo AND nf.noticeType = 'ssucatch' " +
            "ORDER BY s.date DESC")
    Page<SsucatchNoticeDto> findAllWithFavoriteStatus(String studentNo, Pageable pageable);
}
