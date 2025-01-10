package com.example.MaiN.repository;

import com.example.MaiN.dto.FunsysNoticeDto;
import com.example.MaiN.dto.SsucatchNoticeDto;
import com.example.MaiN.entity.FunsysNotice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface FunsysNoticeRepository extends PagingAndSortingRepository<FunsysNotice, Integer>, JpaRepository<FunsysNotice, Integer> {

    @Query("SELECT new com.example.MaiN.dto.FunsysNoticeDto(f.id, f.title, f.link, f.startDate, f.endDate, " +
            "CASE WHEN nf.id IS NOT NULL THEN true ELSE false END) " +
            "FROM FunsysNotice f LEFT JOIN NoticeFavorite nf ON f.id = nf.noticeId " +
            "AND nf.user.studentNo = :studentNo AND nf.noticeType = 'funsys' " +
            "ORDER BY f.startDate DESC")
    Page<FunsysNoticeDto> findAllWithFavoriteStatus(String studentNo, Pageable pageable);
}