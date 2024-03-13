package com.example.MaiN.controller;

import com.example.MaiN.dto.FunsysNotiDto;
import com.example.MaiN.model.funsys_noti_favorites;
import com.example.MaiN.service.FunsysNotiFavoritesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name="funsystem-controller",description = "펀시스템 관련 API")
@RequestMapping("/funsysnoti/favorites")
public class funsys_noti_favorites_Controller {

    @Autowired
    private FunsysNotiFavoritesService funsysNotiFavoritesService;

    @PostMapping("/add/{studentId}/{funsysNotiId}")
    @Operation(summary="펀시스템 게시글 즐겨찾기 추가")
    public ResponseEntity<funsys_noti_favorites> addFavorite(@PathVariable String studentId, @PathVariable int funsysNotiId) {
        funsys_noti_favorites favorite = funsysNotiFavoritesService.addFavorite(studentId, funsysNotiId);
        return ResponseEntity.ok(favorite);
    }
    @DeleteMapping("delete/{studentId}/{funsysNotiId}")
    @Operation(summary="펀시스템 게시글 즐겨찾기 삭제")
    public ResponseEntity<?> deleteFavorite(@PathVariable String studentId, @PathVariable int funsysNotiId) {
        funsysNotiFavoritesService.deleteFavorite(studentId, funsysNotiId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all/{studentId}")
    @Operation(summary="펀시스템 모든 글 조회")
    public ResponseEntity<List<FunsysNotiDto>> getFunsysNotiWithFavorites(@PathVariable int studentId) {
        List<FunsysNotiDto> funsysNotis = funsysNotiFavoritesService.getFunsysNotiWithFavorites(studentId);
        return ResponseEntity.ok(funsysNotis);
    }
}
