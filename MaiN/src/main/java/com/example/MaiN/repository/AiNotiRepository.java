package com.example.MaiN.repository;
import com.example.MaiN.entity.AiNoti;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AiNotiRepository extends PagingAndSortingRepository<AiNoti, Integer>, CrudRepository<AiNoti,Integer> {

}