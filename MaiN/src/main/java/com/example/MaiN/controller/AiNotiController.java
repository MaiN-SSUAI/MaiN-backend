package com.example.MaiN.controller;

import com.example.MaiN.dto.AiNotiDto;
import com.example.MaiN.entity.AiNoti;
import com.example.MaiN.service.AiNotiService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@Getter
@RestController
@RequestMapping(value = "/ai_noti")
public class AiNotiController {

    private final AiNotiService aiNotiService;

    public AiNotiController(AiNotiService aiNotiService) {
        this.aiNotiService = aiNotiService;
    }

    @GetMapping("/all")
    @Operation(summary = "모든 글 불러오기")
    public List<AiNotiDto> list(){
        return aiNotiService.listAll();
    }

    @GetMapping("/noti")
    @Operation(summary = "특정 글 불러오기")
    public Optional<AiNotiDto> getById(@RequestParam("id") int id) {
        return aiNotiService.getNotiById(id);
    }
}