package com.example.MaiN.controller;

import com.example.MaiN.dto.SsuCatchNotiDto;
import com.example.MaiN.entity.SsuCatchNoti;
import com.example.MaiN.repository.SsuCatchNotiRepository;
import com.example.MaiN.service.SsuCatchNotiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Getter
@RestController
@Tag(name="SsuCatchNoti-Controller",description = "슈캐치 관련 API")
@RequestMapping(value = "/ssucatch_noti")
public class SsuCatchNotiController {

    private final SsuCatchNotiService ssuCatchNotiService;

    public SsuCatchNotiController(SsuCatchNotiService ssuCatchNotiService) {
        this.ssuCatchNotiService = ssuCatchNotiService;
    }

    @GetMapping("/all")
    @Operation(summary = "모든 공지 불러오기")
    public List<SsuCatchNotiDto> list(){
        return ssuCatchNotiService.listAll();
    }
    @GetMapping("/noti")
    @Operation(summary = "특정 공지 불러오기")
    public Optional<SsuCatchNotiDto> getById(@RequestParam("id") int id) {
        return ssuCatchNotiService.getNotiByID(id);
    }
}