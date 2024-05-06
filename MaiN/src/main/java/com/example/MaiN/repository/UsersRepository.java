package com.example.MaiN.repository;
import com.example.MaiN.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface UsersRepository extends CrudRepository<Users, String> {
    Optional<Users> findByStudentId(String studentId);
}
