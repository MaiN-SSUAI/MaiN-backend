package com.example.MaiN.repository;
import com.example.MaiN.entity.FunsysNoti;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FunsysNotiRepository extends PagingAndSortingRepository<FunsysNoti,Integer>,CrudRepository<FunsysNoti, Integer> {

}