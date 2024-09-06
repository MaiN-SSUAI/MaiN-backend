package com.example.MaiN.repository;

import com.example.MaiN.entity.NoticeFavorite;
import com.example.MaiN.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface NoticeFavoriteRepository extends JpaRepository<NoticeFavorite, Long> {

    @Query("SELECT nf.noticeId FROM NoticeFavorite nf WHERE nf.user.id = :userId AND nf.noticeType = 'ai'")
    Page<Integer> findAiFavoriteIdsByUserId(@Param("userId") int userId, Pageable pageable);

    @Query("SELECT nf.noticeId FROM NoticeFavorite nf WHERE nf.user.id = :userId AND nf.noticeType = 'funsys'")
    Page<Integer> findFunsysFavoriteIdsByUserId(@Param("userId") int userId, Pageable pageable);

    @Query("SELECT nf.noticeId FROM NoticeFavorite nf WHERE nf.user.id = :userId AND nf.noticeType = 'ssucatch'")
    Page<Integer> findSsucatchFavoriteIdsByUserId(@Param("userId") int userId, Pageable pageable);

    Optional<NoticeFavorite> findByUserIdAndNoticeIdAndNoticeType(int userId, Integer noticeId, String noticeType);
    //Optional<NoticeFavorite> findByStudentNoAndNoticeIdAndNoticeType(User studentNo, int noticeId, String noticeType);

    //boolean existsByStudentNoAndNoticeIdAndNoticeType(User studentNo, int noticeId, String noticeType);
}
