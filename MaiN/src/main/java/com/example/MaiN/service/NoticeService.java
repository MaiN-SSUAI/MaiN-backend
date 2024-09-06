package com.example.MaiN.service;

import com.example.MaiN.dto.*;
import com.example.MaiN.entity.*;
import com.example.MaiN.repository.*;

import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class NoticeService {

    private final AiNoticeRepository aiNoticeRepository;
    private final FunsysNoticeRepository funsysNoticeRepository;
    private final SsucatchNoticeRepository ssucatchNoticeRepository;
    private final NoticeFavoriteRepository noticeFavoriteRepository;
    private final UserRepository userRepository;

    // 공지사항 조회
    public Page<AiNoticeDto> listAllAiNotices(int pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 30);
        return aiNoticeRepository.findAllProjectedBy(pageable);
    }

    public Page<FunsysNoticeDto> listAllFunsysNotices(int pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 30);
        return funsysNoticeRepository.findAllProjectedBy(pageable);
    }

    public Page<SsucatchNoticeDto> listAllSsucatchNotices(int pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 30);
        return ssucatchNoticeRepository.findAllProjectedBy(pageable);
    }

    // 북마크 조회
    public Page<AiNoticeDto> listAiFavorites(String studentNo, int pageNo) {
        return listFavorites(
                studentNo,
                pageNo,
                userId -> noticeFavoriteRepository.findAiFavoriteIdsByUserId(userId, PageRequest.of(pageNo - 1, 30)),
                aiNoticeRepository,
                AiNoticeDto::from
        );
    }
    public Page<FunsysNoticeDto> listFunsysFavorites(String studentNo, int pageNo) {
        return listFavorites(
                studentNo,
                pageNo,
                userId -> noticeFavoriteRepository.findFunsysFavoriteIdsByUserId(userId, PageRequest.of(pageNo - 1, 30)),
                funsysNoticeRepository,
                FunsysNoticeDto::from
        );
    }
    public Page<SsucatchNoticeDto> listSsucatchFavorites(String studentNo, int pageNo) {
        return listFavorites(
                studentNo,
                pageNo,
                userId -> noticeFavoriteRepository.findSsucatchFavoriteIdsByUserId(userId, PageRequest.of(pageNo - 1, 30)),
                ssucatchNoticeRepository,
                SsucatchNoticeDto::from
        );
    }

    // 북마크 추가
    @Transactional
    public void addFavorite(String studentNo, Integer noticeId, String noticeType) {
        User user = userRepository.findByStudentNo(studentNo)
                .orElseThrow(() -> new RuntimeException("User not found with student number: " + studentNo));

        // 이미 존재하는 북마크인지 확인
        Optional<NoticeFavorite> existingFavorite = noticeFavoriteRepository
                .findByUserIdAndNoticeIdAndNoticeType(user.getId(), noticeId, noticeType);
        if (existingFavorite.isPresent()) {
            throw new RuntimeException("Favorite already exists");
        }

        // 공지사항 존재 여부 확인
        boolean noticeExists = checkNoticeExists(noticeId, noticeType);
        if (!noticeExists) {
            throw new RuntimeException("Notice not found");
        }

        NoticeFavorite favorite = new NoticeFavorite(user, noticeId, noticeType);
        noticeFavoriteRepository.save(favorite);
    }

    // 북마크 삭제
    @Transactional
    public void removeFavorite(String studentNo, Integer noticeId, String noticeType) {
        User user = userRepository.findByStudentNo(studentNo)
                .orElseThrow(() -> new RuntimeException("User not found with student number: " + studentNo));

        NoticeFavorite favorite = noticeFavoriteRepository
                .findByUserIdAndNoticeIdAndNoticeType(user.getId(), noticeId, noticeType)
                .orElseThrow(() -> new RuntimeException("Favorite not found"));

        noticeFavoriteRepository.delete(favorite);
    }

    // 공지사항 존재 확인 메소드
    private boolean checkNoticeExists(Integer noticeId, String noticeType) {
        switch (noticeType) {
            case "ai":
                return aiNoticeRepository.existsById(noticeId);
            case "funsys":
                return funsysNoticeRepository.existsById(noticeId);
            case "ssucatch":
                return ssucatchNoticeRepository.existsById(noticeId);
            default:
                throw new IllegalArgumentException("Invalid notice type");
        }
    }

    // 북마크 조회 공통 메소드
    private <T, D, ID> Page<D> listFavorites(String studentNo, int pageNo,
                                             Function<Integer, Page<ID>> favoriteIdsFinder,
                                             JpaRepository<T, ID> noticeRepository,
                                             Function<T, D> dtoMapper) {
        Pageable pageable = PageRequest.of(pageNo - 1, 30);
        User user = userRepository.findByStudentNo(studentNo)
                .orElseThrow(() -> new RuntimeException("User not found with student number: " + studentNo));

        Page<ID> favoriteNoticeIds = favoriteIdsFinder.apply(user.getId());
        if (favoriteNoticeIds.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<T> notices = noticeRepository.findAllById(favoriteNoticeIds.getContent());
        List<D> noticeDtos = notices.stream()
                .map(dtoMapper)
                .collect(Collectors.toList());

        return new PageImpl<>(noticeDtos, pageable, favoriteNoticeIds.getTotalElements());
    }
}