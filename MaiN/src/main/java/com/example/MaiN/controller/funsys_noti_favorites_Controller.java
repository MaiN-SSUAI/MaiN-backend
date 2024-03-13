package com.example.MaiN.controller;

import com.example.MaiN.dto.FunsysNotiDto;
import com.example.MaiN.entity.funsys_noti_favorites;
import com.example.MaiN.service.FunsysNotiFavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funsysnoti/favorites")
public class funsys_noti_favorites_Controller {

    @Autowired
    private FunsysNotiFavoritesService funsysNotiFavoritesService;

    @PostMapping("/add/{studentId}/{funsysNotiId}")
    public ResponseEntity<funsys_noti_favorites> addFavorite(@PathVariable String studentId, @PathVariable int funsysNotiId) {
        funsys_noti_favorites favorite = funsysNotiFavoritesService.addFavorite(studentId, funsysNotiId);
        return ResponseEntity.ok(favorite);
    }
    @DeleteMapping("delete/{studentId}/{funsysNotiId}")
    public ResponseEntity<?> deleteFavorite(@PathVariable String studentId, @PathVariable int funsysNotiId) {
        funsysNotiFavoritesService.deleteFavorite(studentId, funsysNotiId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all/{studentId}")
    public ResponseEntity<List<FunsysNotiDto>> getFunsysNotiWithFavorites(@PathVariable int studentId) {
        List<FunsysNotiDto> funsysNotis = funsysNotiFavoritesService.getFunsysNotiWithFavorites(studentId);
        return ResponseEntity.ok(funsysNotis);
    }
}
