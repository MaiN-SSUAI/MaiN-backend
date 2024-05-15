package com.example.MaiN.repository;
import com.example.MaiN.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface UserRepository extends CrudRepository<User, String> {
    @Query("SELECT u FROM User u WHERE u.studentNo = ?1")
    User findByStudentNo(String studentNo);

    Optional<User> findById(String studentNo);
}
