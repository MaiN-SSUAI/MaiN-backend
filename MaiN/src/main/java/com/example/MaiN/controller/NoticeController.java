package com.example.MaiN.controller;

import com.example.MaiN.dto.AiNoticeDto;
import com.example.MaiN.dto.FavoriteRequestDto;
import com.example.MaiN.dto.FunsysNoticeDto;
import com.example.MaiN.dto.SsucatchNoticeDto;
import com.example.MaiN.service.NoticeService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/ai")
    @Operation(summary = "AI융합학부 공지사항 조회하기")
    public ResponseEntity<Page<AiNoticeDto>> listAI(@RequestParam("pageNo") int pageNo){
        return ResponseEntity.ok(noticeService.listAllAiNotices(pageNo));
    }

    @GetMapping("/funsys")
    @Operation(summary = "펀시스템 공지사항 조회하기")
    public ResponseEntity<Page<FunsysNoticeDto>> listFunsys(@RequestParam("pageNo") int pageNo){
        return ResponseEntity.ok(noticeService.listAllFunsysNotices(pageNo));
    }

    @GetMapping("/ssucatch")
    @Operation(summary = "슈캐치 공지사항 조회하기")
    public ResponseEntity<Page<SsucatchNoticeDto>> listSsucatch(@RequestParam("pageNo") int pageNo){
        return ResponseEntity.ok(noticeService.listAllSsucatchNotices(pageNo));
    }

    @GetMapping("/ai/favorites")
    @Operation(summary = "AI융합학부 북마크 조회하기")
    public ResponseEntity<Page<AiNoticeDto>> listAiFavorites(
            @RequestParam String studentNo,
            @RequestParam int pageNo) {
        return ResponseEntity.ok(noticeService.listAiFavorites(studentNo, pageNo));
    }

    @GetMapping("/funsys/favorites")
    @Operation(summary = "펀시스템 북마크 조회하기")
    public ResponseEntity<Page<FunsysNoticeDto>> listFunsysFavorites(
            @RequestParam String studentNo,
            @RequestParam int pageNo) {
        return ResponseEntity.ok(noticeService.listFunsysFavorites(studentNo, pageNo));
    }

    @GetMapping("/ssucatch/favorites")
    @Operation(summary = "슈캐치 북마크 조회하기")
    public ResponseEntity<Page<SsucatchNoticeDto>> listSsucatchFavorites(
            @RequestParam String studentNo,
            @RequestParam int pageNo) {
        return ResponseEntity.ok(noticeService.listSsucatchFavorites(studentNo, pageNo));
    }

    @PostMapping("/favorite")
    @Operation(summary = "북마크 추가하기")
    public ResponseEntity<?> addFavorite(@RequestBody FavoriteRequestDto requestDto) {
        try {
            noticeService.addFavorite(requestDto.getStudentNo(), requestDto.getNoticeId(), requestDto.getNoticeType());
            return ResponseEntity.ok("Favorite added successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/favorite")
    @Operation(summary = "북마크 삭제하기")
    public ResponseEntity<?> removeFavorite(@RequestBody FavoriteRequestDto requestDto) {
        try {
            noticeService.removeFavorite(requestDto.getStudentNo(), requestDto.getNoticeId(), requestDto.getNoticeType());
            return ResponseEntity.ok("Favorite removed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
