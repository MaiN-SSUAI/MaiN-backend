package com.example.MaiN.controller;

import com.example.MaiN.dto.FunsysNotiDto;
import com.example.MaiN.dto.FunsysNotiFavorDto;
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

    @PostMapping("/add")
    @Operation(summary = "북마크 추가")
    public ResponseEntity<FunsysNotiFavor> addFavorite(@RequestBody FunsysNotiFavorDto dto) {
        FunsysNotiFavor favorite = funsysNotiFavorService.addFavorite(dto);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/delete")
    @Operation(summary = "북마크 삭제")
    public ResponseEntity<?> deleteFavorite(@RequestBody FunsysNotiFavorDto dto) {
        funsysNotiFavorService.deleteFavorite(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all/{studentNo}")
    @Operation(summary = "모든 북마크 보기")
    public ResponseEntity<List<FunsysNotiDto>> getFunsysNotiWithFavorites(@PathVariable int studentNo, @RequestParam("pageNo") int pageNo) {
        List<FunsysNotiDto> funsysNotis = funsysNotiFavorService.getFunsysNotiWithFavorites(studentNo, pageNo);
        return ResponseEntity.ok(funsysNotis);
    }

    @GetMapping("/all/dev/{studentNo}")
    @Operation(summary = "모든 북마크 보기")
    public ResponseEntity<List<FunsysNotiDto>> getFunsysNotiWithFavoritesDev(@PathVariable int studentNo) {
        List<FunsysNotiDto> funsysNotis = funsysNotiFavorService.getFunsysNotiWithFavoritesDev(studentNo);
        return ResponseEntity.ok(funsysNotis);
    }
}
