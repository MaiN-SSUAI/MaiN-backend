package com.example.MaiN.repository;
import com.example.MaiN.dto.SsuCatchNotiDto;
import com.example.MaiN.entity.SsuCatchNoti;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface SsuCatchNotiRepository extends PagingAndSortingRepository<SsuCatchNoti,Integer>,CrudRepository<SsuCatchNoti, Integer> {
    @Query("SELECT new com.example.MaiN.dto.SsuCatchNotiDto(s.id, s.title, s.link, s.progress, s.category, s.date," +
            "(SELECT COUNT(fav) > 0 FROM SsuCatchNotiFavor fav WHERE fav.ssuCatchNoti.id = s.id)) " +
            "FROM SsuCatchNoti s ORDER BY s.date DESC")
    List<SsuCatchNotiDto> findAllProjectedBy(Sort sort);

    @Query("SELECT new com.example.MaiN.dto.SsuCatchNotiDto(s.id, s.title, s.link, s.progress, s.category, s.date, " +
            "CASE WHEN (COUNT(fav) > 0) THEN true ELSE false END) " +
            "FROM SsuCatchNoti s LEFT JOIN s.favoritesSet fav WHERE s.id = :id GROUP BY s.id")
    Optional<SsuCatchNotiDto> findDtoById(int id);
}