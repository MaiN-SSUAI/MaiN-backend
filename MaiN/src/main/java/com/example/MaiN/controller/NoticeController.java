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

    @GetMapping
    @Operation(summary = "공지사항 조회하기")
    public ResponseEntity<?> listAllNotices(
            @RequestParam String noticeType,
            @RequestParam String studentNo,
            @RequestParam(defaultValue = "1") int pageNo) {
        try {
            Page<?> notices = noticeService.listAllNotices(noticeType, studentNo, pageNo);
            return ResponseEntity.ok(notices);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid notice type: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/favorite")
    @Operation(summary = "북마크 등록하기")
    public ResponseEntity<?> addFavorite(@RequestBody FavoriteRequestDto requestDto) {
        try {
            noticeService.addFavorite(requestDto.getStudentNo(), requestDto.getNoticeId(), requestDto.getNoticeType());
            return ResponseEntity.ok("북마크 등록 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/favorite")
    @Operation(summary = "북마크 삭제하기")
    public ResponseEntity<?> removeFavorite(@RequestBody FavoriteRequestDto requestDto) {
        try {
            noticeService.removeFavorite(requestDto.getStudentNo(), requestDto.getNoticeId(), requestDto.getNoticeType());
            return ResponseEntity.ok("북마크 삭제 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error occurred: " + e.getMessage());
        }
    }

//    @GetMapping("/ai/favorites")
//    @Operation(summary = "AI융합학부 북마크 조회하기")
//    public ResponseEntity<Page<AiNoticeDto>> listAiFavorites(
//            @RequestParam String studentNo,
//            @RequestParam int pageNo) {
//        return ResponseEntity.ok(noticeService.listAiFavorites(studentNo, pageNo));
//    }
//
//    @GetMapping("/funsys/favorites")
//    @Operation(summary = "펀시스템 북마크 조회하기")
//    public ResponseEntity<Page<FunsysNoticeDto>> listFunsysFavorites(
//            @RequestParam String studentNo,
//            @RequestParam int pageNo) {
//        return ResponseEntity.ok(noticeService.listFunsysFavorites(studentNo, pageNo));
//    }
//
//    @GetMapping("/ssucatch/favorites")
//    @Operation(summary = "슈캐치 북마크 조회하기")
//    public ResponseEntity<Page<SsucatchNoticeDto>> listSsucatchFavorites(
//            @RequestParam String studentNo,
//            @RequestParam int pageNo) {
//        return ResponseEntity.ok(noticeService.listSsucatchFavorites(studentNo, pageNo));
//    }
}
