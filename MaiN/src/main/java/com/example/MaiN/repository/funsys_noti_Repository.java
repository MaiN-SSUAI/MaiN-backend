package com.example.MaiN.repository;
import com.example.MaiN.model.funsys_noti;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface funsys_noti_Repository extends PagingAndSortingRepository<funsys_noti,Integer>,CrudRepository<funsys_noti, Integer> {

}