package com.example.MaiN.controller;

import com.example.MaiN.dto.SsucatchNotiDto;
import com.example.MaiN.model.ssucatch_noti_favorites;
import com.example.MaiN.service.SsucatchNotiFavoritesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name="ssucatch-controller",description = "슈캐치 공지사항 관련 API")
@RequestMapping("/ssucatchnoti/favorites")
public class ssucatch_noti_favorites_Controller {

    @Autowired
    private SsucatchNotiFavoritesService ssucatchNotiFavoritesService;

    @PostMapping("/add/{studentId}/{ssucatchNotiId}")
    @Operation(summary="슈캐치 공지사항 즐겨찾기 추가")
    public ResponseEntity<ssucatch_noti_favorites> addFavorite(@PathVariable String studentId, @PathVariable int ssucatchNotiId) {
        ssucatch_noti_favorites favorite = ssucatchNotiFavoritesService.addFavorite(studentId, ssucatchNotiId);
        return ResponseEntity.ok(favorite);
    }
    @DeleteMapping("delete/{studentId}/{ssucatchNotiId}")
    @Operation(summary="슈캐치 공지사항 즐겨찾기 삭제")
    public ResponseEntity<?> deleteFavorite(@PathVariable String studentId, @PathVariable int ssucatchNotiId) {
        ssucatchNotiFavoritesService.deleteFavorite(studentId, ssucatchNotiId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all/{studentId}")
    @Operation(summary="슈캐치 모든 공지사항 조회")
    public ResponseEntity<List<SsucatchNotiDto>> getSsucatchNotiWithFavorites(@PathVariable String studentId) {
        List<SsucatchNotiDto> ssucatchNotis = ssucatchNotiFavoritesService.getSsucatchNotiWithFavorites(studentId);
        return ResponseEntity.ok(ssucatchNotis);
    }
}
