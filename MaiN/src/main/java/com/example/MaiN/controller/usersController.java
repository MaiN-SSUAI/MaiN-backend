package com.example.MaiN.controller;

import com.example.MaiN.dto.UserDto;
import com.example.MaiN.model.users;
import com.example.MaiN.repository.users_Repository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name="users-controller",description = "로그인 관련 API")
@RequestMapping("/users")
public class usersController {
    private final users_Repository users_Repository;

    public usersController(users_Repository usersRepository) {
        this.users_Repository = usersRepository;
    }

    //사용자 학번 모두 불러오기
    @GetMapping("/all")
    @Operation(summary="모든 사용자 학번 불러오기")
    public Iterable<users> list() { return users_Repository.findAll(); }

    //학번 -> db 에 저장
    @PostMapping("/add")
    @Operation(summary="로그인 성공시 db에 학번 추가")
    public String postUserInfo(@RequestBody UserDto userDto) {
        users foundUser = users_Repository.findByStudent_id(userDto.getStudent_id());
        if (foundUser != null) {
            return "user with this student_id already exists"; //중복 저장 방지
        }
        else {
            users users = userDto.toEntity();
            users saved = users_Repository.save(users);
            return "user info saved";
        }
    }
}
