package com.example.MaiN.controller;

import com.example.MaiN.dto.SsucatchNotiDto;
import com.example.MaiN.entity.ssucatch_noti_favorites;
import com.example.MaiN.service.SsucatchNotiFavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ssucatchnoti/favorites")
public class ssucatch_noti_favorites_Controller {

    @Autowired
    private SsucatchNotiFavoritesService ssucatchNotiFavoritesService;

    @PostMapping("/add/{studentId}/{ssucatchNotiId}")
    public ResponseEntity<ssucatch_noti_favorites> addFavorite(@PathVariable String studentId, @PathVariable int ssucatchNotiId) {
        ssucatch_noti_favorites favorite = ssucatchNotiFavoritesService.addFavorite(studentId, ssucatchNotiId);
        return ResponseEntity.ok(favorite);
    }
    @DeleteMapping("delete/{studentId}/{ssucatchNotiId}")
    public ResponseEntity<?> deleteFavorite(@PathVariable String studentId, @PathVariable int ssucatchNotiId) {
        ssucatchNotiFavoritesService.deleteFavorite(studentId, ssucatchNotiId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all/{studentId}")
    public ResponseEntity<List<SsucatchNotiDto>> getSsucatchNotiWithFavorites(@PathVariable String studentId) {
        List<SsucatchNotiDto> ssucatchNotis = ssucatchNotiFavoritesService.getSsucatchNotiWithFavorites(studentId);
        return ResponseEntity.ok(ssucatchNotis);
    }
}
