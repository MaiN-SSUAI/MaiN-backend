package com.example.MaiN.controller;

import com.example.MaiN.dto.AiNotiDto;
import com.example.MaiN.model.ai_noti_favorites;
import com.example.MaiN.service.AiNotiFavoritesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController

@Tag(name="ai_noti-controller",description = "AI융합학부 공지사항 관련 API")
@RequestMapping("/ainoti/favorites")
public class ai_noti_favorites_Controller {

    @Autowired
    private AiNotiFavoritesService aiNotiFavoritesService;


    @PostMapping("/add/{studentId}/{aiNotiId}")
    @Operation(summary="AI융합학부 공지사항 즐겨찾기 추가")
    public ResponseEntity<ai_noti_favorites> addFavorite(@PathVariable String studentId, @PathVariable int aiNotiId) {
        ai_noti_favorites favorite = aiNotiFavoritesService.addFavorite(studentId, aiNotiId);
        return ResponseEntity.ok(favorite);
    }
    @DeleteMapping("delete/{studentId}/{aiNotiId}")
    @Operation(summary="AI융합학부 공지사항 즐겨찾기 삭제")
    public ResponseEntity<?> deleteFavorite(@PathVariable String studentId, @PathVariable int aiNotiId) {
        aiNotiFavoritesService.deleteFavorite(studentId, aiNotiId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all/{studentId}")
    @Operation(summary="AI융합학부 공지사항 모든 공지 조회")
    public ResponseEntity<List<AiNotiDto>> getAiNotiWithFavorites(@PathVariable String studentId) {
        List<AiNotiDto> aiNotis = aiNotiFavoritesService.getAiNotiWithFavorites(studentId);
        return ResponseEntity.ok(aiNotis);
    }
}
