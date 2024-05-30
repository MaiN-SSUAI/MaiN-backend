package com.example.MaiN.repository;
import com.example.MaiN.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import java.util.List;

import java.util.Optional;


public interface UserRepository extends CrudRepository<User, String> {

    @Query("SELECT u FROM User u WHERE u.studentNo = ?1")
    Optional<User> findByStudentNo(String studentNo);

    @Query(value = "SELECT u FROM User u LEFT JOIN FETCH u.funsysNoti WHERE u.studentNo = :studentNo", nativeQuery = true)
    Optional<User> findUserWithFunsysNotiByNo(@Param("studentNo") String studentNo);

//    Optional<User> findById(String studentNo);
}
