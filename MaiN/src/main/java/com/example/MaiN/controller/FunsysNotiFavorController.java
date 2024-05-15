package com.example.MaiN.controller;

import com.example.MaiN.dto.FunsysNotiDto;
import com.example.MaiN.entity.FunsysNotiFavor;
import com.example.MaiN.service.FunsysNotiFavorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name="FunsysNoti-Favorite-Controller",description = "펀시스템 북마크 관련 API")
@RequestMapping("/funsysnoti/favorites")
public class FunsysNotiFavorController {

    @Autowired
    private FunsysNotiFavorService funsysNotiFavorService;

    @PostMapping("/add/{studentNo}/{funsysNotiId}")
    @Operation(summary = "북마크 추가")
    public ResponseEntity<FunsysNotiFavor> addFavorite(@PathVariable String studentNo, @PathVariable int funsysNotiId) {
        FunsysNotiFavor favorite = funsysNotiFavorService.addFavorite(studentNo, funsysNotiId);
        return ResponseEntity.ok(favorite);
    }
    @DeleteMapping("delete/{studentNo}/{funsysNotiId}")
    @Operation(summary = "북마크 삭제")
    public ResponseEntity<?> deleteFavorite(@PathVariable String studentNo, @PathVariable int funsysNotiId) {
        funsysNotiFavorService.deleteFavorite(studentNo, funsysNotiId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all/{studentNo}")
    @Operation(summary = "모든 북마크 보기")
    public ResponseEntity<List<FunsysNotiDto>> getFunsysNotiWithFavorites(@PathVariable int studentNo) {
        List<FunsysNotiDto> funsysNotis = funsysNotiFavorService.getFunsysNotiWithFavorites(studentNo);
        return ResponseEntity.ok(funsysNotis);
    }
}
