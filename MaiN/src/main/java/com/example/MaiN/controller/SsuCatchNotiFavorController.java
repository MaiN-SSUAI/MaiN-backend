package com.example.MaiN.controller;

import com.example.MaiN.dto.SsuCatchNotiDto;
import com.example.MaiN.dto.SsuCatchNotiFavorDto;
import com.example.MaiN.entity.SsuCatchNotiFavor;
import com.example.MaiN.service.SsuCatchNotiFavorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name="SsucatchNoti-Favorite-Controller",description = "슈캐치 북마크 관련 API")
@RequestMapping("/ssucatchnoti/favorites")
public class SsuCatchNotiFavorController {

    @Autowired
    private SsuCatchNotiFavorService ssuCatchNotiFavorService;

    @PostMapping("/add")
    @Operation(summary = "슈캐치 북마크 추가")
    public ResponseEntity<SsuCatchNotiFavor> addFavorite(@RequestBody SsuCatchNotiFavorDto dto) {
        SsuCatchNotiFavor favorite = ssuCatchNotiFavorService.addFavorite(dto);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("delete/{studentNo}/{ssuCatchNotiId}")
    @Operation(summary = "슈캐치 북마크 삭제")
    public ResponseEntity<?> deleteFavorite(@PathVariable String studentNo, @PathVariable int ssuCatchNotiId) {
        ssuCatchNotiFavorService.deleteFavorite(studentNo, ssuCatchNotiId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all/{studentNo}")
    @Operation(summary = "모든 북마크 보기")
    public ResponseEntity<List<SsuCatchNotiDto>> getSsuCatchNotiWithFavorites(@PathVariable String studentNo) {
        List<SsuCatchNotiDto> ssuCatchNotilist = ssuCatchNotiFavorService.getSsuCatchNotiWithFavorites(studentNo);
        return ResponseEntity.ok(ssuCatchNotilist);
    }
}
