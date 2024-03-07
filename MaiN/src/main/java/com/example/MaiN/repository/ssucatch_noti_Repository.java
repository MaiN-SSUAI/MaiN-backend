package com.example.MaiN.repository;
import com.example.MaiN.model.ssucatch_noti;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ssucatch_noti_Repository extends PagingAndSortingRepository<ssucatch_noti,Integer>,CrudRepository<ssucatch_noti, Integer> {

}