package com.example.MaiN.repository;
import com.example.MaiN.model.ai_noti;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ai_noti_Repository extends PagingAndSortingRepository<ai_noti, Integer>, CrudRepository<ai_noti,Integer> {

}