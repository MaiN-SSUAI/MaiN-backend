package com.example.MaiN.controller;

import com.example.MaiN.dto.AiNotiDto;
import com.example.MaiN.model.ai_noti_favorites;
import com.example.MaiN.service.AiNotiFavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ainoti/favorites")
public class ai_noti_favorites_Controller {

    @Autowired
    private AiNotiFavoritesService aiNotiFavoritesService;

    @PostMapping("/add/{studentId}/{aiNotiId}")
    public ResponseEntity<ai_noti_favorites> addFavorite(@PathVariable String studentId, @PathVariable int aiNotiId) {
        ai_noti_favorites favorite = aiNotiFavoritesService.addFavorite(studentId, aiNotiId);
        return ResponseEntity.ok(favorite);
    }
    @DeleteMapping("delete/{studentId}/{aiNotiId}")
    public ResponseEntity<?> deleteFavorite(@PathVariable String studentId, @PathVariable int aiNotiId) {
        aiNotiFavoritesService.deleteFavorite(studentId, aiNotiId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all/{studentId}")
    public ResponseEntity<List<AiNotiDto>> getAiNotiWithFavorites(@PathVariable String studentId) {
        List<AiNotiDto> aiNotis = aiNotiFavoritesService.getAiNotiWithFavorites(studentId);
        return ResponseEntity.ok(aiNotis);
    }
}
