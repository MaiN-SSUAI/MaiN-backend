package com.example.MaiN.repository;

import com.example.MaiN.dto.SsucatchNoticeDto;
import com.example.MaiN.entity.SsucatchNotice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface SsucatchNoticeRepository extends PagingAndSortingRepository<SsucatchNotice, Integer>, JpaRepository<SsucatchNotice, Integer> {

    @Query("SELECT new com.example.MaiN.dto.SsucatchNoticeDto(s.id, s.title, s.link, s.progress, s.category, s.date)" +
            "FROM SsucatchNotice s ORDER BY s.date DESC")
    Page<SsucatchNoticeDto> findAllProjectedBy(Pageable pageable);

}
