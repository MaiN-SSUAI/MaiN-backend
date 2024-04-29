package com.example.MaiN.controller;

import com.example.MaiN.dto.FunsysNotiDto;
import com.example.MaiN.entity.FunsysNotiFavor;
import com.example.MaiN.service.FunsysNotiFavorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funsysnoti/favorites")
public class FunsysNotiFavorController {

    @Autowired
    private FunsysNotiFavorService funsysNotiFavorService;

    @PostMapping("/add/{studentId}/{funsysNotiId}")
    public ResponseEntity<FunsysNotiFavor> addFavorite(@PathVariable String studentId, @PathVariable int funsysNotiId) {
        FunsysNotiFavor favorite = funsysNotiFavorService.addFavorite(studentId, funsysNotiId);
        return ResponseEntity.ok(favorite);
    }
    @DeleteMapping("delete/{studentId}/{funsysNotiId}")
    public ResponseEntity<?> deleteFavorite(@PathVariable String studentId, @PathVariable int funsysNotiId) {
        funsysNotiFavorService.deleteFavorite(studentId, funsysNotiId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all/{studentId}")
    public ResponseEntity<List<FunsysNotiDto>> getFunsysNotiWithFavorites(@PathVariable int studentId) {
        List<FunsysNotiDto> funsysNotis = funsysNotiFavorService.getFunsysNotiWithFavorites(studentId);
        return ResponseEntity.ok(funsysNotis);
    }
}
