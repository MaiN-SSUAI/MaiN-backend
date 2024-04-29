package com.example.MaiN.controller;

import com.example.MaiN.dto.SsuCatchNotiDto;
import com.example.MaiN.entity.SsuCatchNotiFavor;
import com.example.MaiN.service.SsuCatchNotiFavorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ssucatchnoti/favorites")
public class SsuCatchNotiFavorController {

    @Autowired
    private SsuCatchNotiFavorService ssuCatchNotiFavorService;

    @PostMapping("/add/{studentId}/{ssuCatchNotiId}")
    public ResponseEntity<SsuCatchNotiFavor> addFavorite(@PathVariable String studentId, @PathVariable int ssuCatchNotiId) {
        SsuCatchNotiFavor favorite = ssuCatchNotiFavorService.addFavorite(studentId, ssuCatchNotiId);
        return ResponseEntity.ok(favorite);
    }
    @DeleteMapping("delete/{studentId}/{ssuCatchNotiId}")
    public ResponseEntity<?> deleteFavorite(@PathVariable String studentId, @PathVariable int ssuCatchNotiId) {
        ssuCatchNotiFavorService.deleteFavorite(studentId, ssuCatchNotiId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all/{studentId}")
    public ResponseEntity<List<SsuCatchNotiDto>> getSsuCatchNotiWithFavorites(@PathVariable String studentId) {
        List<SsuCatchNotiDto> ssuCatchNotilist = ssuCatchNotiFavorService.getSsuCatchNotiWithFavorites(studentId);
        return ResponseEntity.ok(ssuCatchNotilist);
    }
}
