package com.example.MaiN.controller;

import com.example.MaiN.dto.AiNotiDto;
import com.example.MaiN.entity.AiNotiFavor;
import com.example.MaiN.service.AiNotiFavorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ainoti/favorites")
public class AiNotiFavorController {

    @Autowired
    private AiNotiFavorService aiNotiFavorService;

    @PostMapping("/add/{studentId}/{aiNotiId}")
    public ResponseEntity<AiNotiFavor> addFavorite(@PathVariable String studentId, @PathVariable int aiNotiId) {
        AiNotiFavor favorite = aiNotiFavorService.addFavorite(studentId, aiNotiId);
        return ResponseEntity.ok(favorite);
    }
    @DeleteMapping("delete/{studentId}/{aiNotiId}")
    public ResponseEntity<?> deleteFavorite(@PathVariable String studentId, @PathVariable int aiNotiId) {
        aiNotiFavorService.deleteFavorite(studentId, aiNotiId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all/{studentId}")
    public ResponseEntity<List<AiNotiDto>> getAiNotiWithFavorites(@PathVariable String studentId) {
        List<AiNotiDto> aiNotilist = aiNotiFavorService.getAiNotiWithFavorites(studentId);
        return ResponseEntity.ok(aiNotilist);
    }
}