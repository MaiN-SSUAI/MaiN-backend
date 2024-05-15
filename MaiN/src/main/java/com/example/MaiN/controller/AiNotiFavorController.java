package com.example.MaiN.controller;

import com.example.MaiN.dto.AiNotiDto;
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

    @PostMapping("/add/{studentNo}/{aiNotiId}")
    @Operation(summary="북마크 추가")
    public ResponseEntity<AiNotiFavor> addFavorite(@PathVariable String studentNo, @PathVariable int aiNotiId) {
        AiNotiFavor favorite = aiNotiFavorService.addFavorite(studentNo, aiNotiId);
        return ResponseEntity.ok(favorite);
    }
    @DeleteMapping("delete/{studentNo}/{aiNotiId}")
    @Operation(summary="북마크 삭제")
    public ResponseEntity<?> deleteFavorite(@PathVariable String studentNo, @PathVariable int aiNotiId) {
        aiNotiFavorService.deleteFavorite(studentNo, aiNotiId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all/{studentNo}")
    @Operation(summary = "모든 북마크 보기")
    public ResponseEntity<List<AiNotiDto>> getAiNotiWithFavorites(@PathVariable String studentNo) {
        List<AiNotiDto> aiNotilist = aiNotiFavorService.getAiNotiWithFavorites(studentNo);
        return ResponseEntity.ok(aiNotilist);
    }
}
