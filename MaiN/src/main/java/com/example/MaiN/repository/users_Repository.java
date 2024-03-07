package com.example.MaiN.repository;
import com.example.MaiN.model.users;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface users_Repository extends CrudRepository<users, String> {
    @Query("SELECT u FROM users u WHERE u.student_id = ?1")
    users findByStudent_id(String student_id);
}
