package com.example.MaiN.controller;

import com.example.MaiN.dto.FunsysNotiDto;
import com.example.MaiN.entity.FunsysNoti;
import com.example.MaiN.service.FunsysNotiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Getter
@RestController
@Tag(name="FunsysNoti-Controller", description = "펀시스템 관련 API")
@RequestMapping(value = "/funsys_noti")
public class FunsysNotiController {
    private final FunsysNotiService funsysNotiService;

    public FunsysNotiController(FunsysNotiService funsysNotiService) {
        this.funsysNotiService = funsysNotiService;
    }

    @GetMapping("/all")
    @Operation(summary = "모든 글 불러오기")
    public List<FunsysNotiDto> list(){
        return funsysNotiService.listAll();
    }


    @GetMapping("/noti")
    @Operation(summary = "특정 글 불러오기")
    public Optional<FunsysNotiDto> getById(@RequestParam("id") int id) {
        return funsysNotiService.getNotiById(id);
    }
}