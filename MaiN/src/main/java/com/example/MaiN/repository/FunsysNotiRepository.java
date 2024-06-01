package com.example.MaiN.repository;
import com.example.MaiN.dto.FunsysNotiDto;
import com.example.MaiN.entity.FunsysNoti;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FunsysNotiRepository extends PagingAndSortingRepository<FunsysNoti, Integer>, CrudRepository<FunsysNoti, Integer> {
    @Query("SELECT new com.example.MaiN.dto.FunsysNotiDto(f.id, f.title, f.link, f.startDate, f.endDate, " +
            "(SELECT COUNT(fav) > 0 FROM FunsysNotiFavor fav WHERE fav.funsysNoti.id = f.id)) " +
            "FROM FunsysNoti f ORDER BY f.startDate DESC")
    List<FunsysNotiDto> findAllProjectedBy(Sort sort);

    @Query("SELECT new com.example.MaiN.dto.FunsysNotiDto(f.id, f.title, f.link, f.startDate, f.endDate, " +
            "CASE WHEN (COUNT(fav) > 0) THEN true ELSE false END) " +
            "FROM FunsysNoti f LEFT JOIN f.favoritesSet fav WHERE f.id = :id GROUP BY f.id")
    Optional<FunsysNotiDto> findDtoById(int id);
}