package com.example.MaiN.controller;

import com.example.MaiN.dto.AiNotiDto;
import com.example.MaiN.dto.AiNotiFavorDto;
import com.example.MaiN.entity.AiNotiFavor;
import com.example.MaiN.service.AiNotiFavorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name="AiNoti-Favorite-Controller",description = "AI융합학부 공지사항 북마크 관련 API")
@RequestMapping("/ainoti/favorites")
public class AiNotiFavorController {

    @Autowired
    private AiNotiFavorService aiNotiFavorService;

    @PostMapping("/add")
    @Operation(summary="북마크 추가")
    public ResponseEntity<AiNotiFavor> addFavorite(@RequestBody AiNotiFavorDto aiNotiFavorDto) {
        AiNotiFavor favorite = aiNotiFavorService.addFavorite(aiNotiFavorDto);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/delete")
    @Operation(summary="북마크 삭제")
    public ResponseEntity<?> deleteFavorite(@RequestBody AiNotiFavorDto aiNotiFavorDto) {
        aiNotiFavorService.deleteFavorite(aiNotiFavorDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all/{studentNo}")
    @Operation(summary = "모든 북마크 보기")
    public ResponseEntity<List<AiNotiDto>>  getAiNotiWithFavorites(@PathVariable int studentNo, @RequestParam("pageNo") int pageNo) {
        List<AiNotiDto> aiNotilist = aiNotiFavorService.getAiNotiWithFavorites(studentNo, pageNo);
        return ResponseEntity.ok(aiNotilist);
    }
}
