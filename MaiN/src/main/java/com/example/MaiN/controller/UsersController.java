package com.example.MaiN.controller;

import com.example.MaiN.dto.*;
import com.example.MaiN.entity.User;
import com.example.MaiN.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name="Users-Controller",description = "로그인 관련 API")
@RequestMapping("/users")
public class UsersController {
    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    //사용자 학번 모두 불러오기
    @GetMapping("/all")
    public Iterable<User> list() { return usersService.findAllUsers(); }

    //학번 -> db 에 저장
//    @PostMapping("/add")
//    public String postUserInfo(@RequestBody UserDto UserDto) {
//        return usersService.addUser(UserDto);
//    }

    @PostMapping("/login")
    @Operation(summary = "accessToken, refreshToken,학번 응답 받기")
    public ResponseEntity<?> login(@RequestBody UsaintRequestDto usaintRequestDto) throws Exception {
        Map<String,Object> stdInfo = usersService.usaintAuthService(usaintRequestDto);

        String stdMajor = (String) stdInfo.get("학부");
        String stdId = (String) stdInfo.get("학번");

        if(stdMajor.equals("AI융합학부")) {
            LoginRequestDto loginRequestDto = new LoginRequestDto();
            loginRequestDto.setstudentNo(stdId);
            return ResponseEntity.ok().body(usersService.login(loginRequestDto));
        }
        else{
            return ResponseEntity.badRequest().body("AI융합학부 학생이 아님");
        }
    }

    //토큰 재발급 요청
    @PostMapping("/reissue")
    @Operation(summary = "accessToken 재발급 요청")
    public ResponseEntity<TokenDto> reissue(@RequestBody TokenRequestDto tokenRequestDto) throws Exception {
        return ResponseEntity.ok().body(usersService.reissue(tokenRequestDto));
    }

    //로그아웃
    @DeleteMapping("/logout")
    @Operation(summary = "로그아웃")
    public ResponseEntity<?> logout(@RequestBody UserDto userDto){
        String stdNo = userDto.getStudentNo();
        boolean result = usersService.logout(stdNo);
        if(result) return ResponseEntity.ok().body( "로그아웃 성공");
        else return ResponseEntity.badRequest().body("이미 로그아웃 된 사용자");
    }
}
