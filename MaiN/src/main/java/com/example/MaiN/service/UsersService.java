package com.example.MaiN.service;

import com.example.MaiN.dto.UsersDto;
import com.example.MaiN.entity.Users;
import com.example.MaiN.repository.UsersRepository;
import org.springframework.stereotype.Service;

@Service
public class UsersService {
    private final UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository){
        this.usersRepository = usersRepository;
    }

    public Iterable<Users> findAllUsers(){
        return usersRepository.findAll();
    }

    public String addUser(UsersDto usersDto){
        Users foundUsers = usersRepository.findByStudentId(usersDto.getStudentId());
        if (foundUsers != null) return "user with this student_id already exists"; //중복 저장 방지
        else {
            Users users = usersDto.toEntity();
            Users saved = usersRepository.save(users);
            return "user info saved";
        }
    }
}
