package com.example.MaiN.repository;
import com.example.MaiN.dto.AiNotiDto;
import com.example.MaiN.entity.AiNoti;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface AiNotiRepository extends PagingAndSortingRepository<AiNoti, Integer>, JpaRepository<AiNoti,Integer> {

    @Query("SELECT new com.example.MaiN.dto.AiNotiDto(a.id, a.title, a.link, a.date, " +
            "CASE WHEN (COUNT(fav) > 0) THEN true ELSE false END) " +
            "FROM AiNoti a LEFT JOIN a.favoritesSet fav WHERE a.id = :id GROUP BY a.id, a.title, a.link, a.date")
    Optional<AiNotiDto> findDtoById(int id);

    @Query("SELECT new com.example.MaiN.dto.AiNotiDto(a.id, a.title, a.link, a.date, " +
            "(SELECT COUNT(fav) > 0 FROM AiNotiFavor fav WHERE fav.aiNoti.id = a.id)) " +
            "FROM AiNoti a ORDER BY a.date DESC")
    List<AiNotiDto> findAllProjectedBy(Sort sort);
}