package com.example.MaiN.repository;
import com.example.MaiN.entity.SsuCatchNoti;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SsuCatchNotiRepository extends PagingAndSortingRepository<SsuCatchNoti,Integer>,CrudRepository<SsuCatchNoti, Integer> {

}