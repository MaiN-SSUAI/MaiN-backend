package com.example.MaiN.controller;

import com.example.MaiN.dto.UsersDto;
import com.example.MaiN.entity.Users;
import com.example.MaiN.service.UsersService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UsersController {
    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    //사용자 학번 모두 불러오기
    @GetMapping("/all")
    public Iterable<Users> list() { return usersService.findAllUsers(); }

    //학번 -> db 에 저장
    @PostMapping("/add")
    public String postUserInfo(@RequestBody UsersDto usersDto) {
        return usersService.addUser(usersDto);
    }
}
