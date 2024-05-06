package com.example.MaiN.controller;

import com.example.MaiN.dto.LoginRequestDto;
import com.example.MaiN.dto.LoginReturnDto;
import com.example.MaiN.dto.UsaintRequestDto;
import com.example.MaiN.dto.UsersDto;
import com.example.MaiN.entity.Users;
import com.example.MaiN.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @PostMapping("/login")
    public String login(@RequestBody UsaintRequestDto usaintRequestDto) throws Exception {
        Map<String,Object> stdInfo = usersService.usaintAuthService(usaintRequestDto);

        String stdMajor = (String) stdInfo.get("학부");
        String stdId = (String) stdInfo.get("학번");

        if(stdMajor.equals("AI융합학부")) {
            LoginRequestDto loginRequestDto = new LoginRequestDto();
            loginRequestDto.setStudentId(stdId);
            return usersService.login(loginRequestDto);
        }
        else{
            return "AI융합학부 학생이 아님";
        }
    }
}
