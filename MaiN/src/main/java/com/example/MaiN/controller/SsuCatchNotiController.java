package com.example.MaiN.controller;

import com.example.MaiN.entity.SsuCatchNoti;
import com.example.MaiN.repository.SsuCatchNotiRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Getter
@RestController
@Tag(name="SsuCatchNoti-Controller",description = "슈캐치 관련 API")
@RequestMapping(value = "/ssucatch_noti")
public class SsuCatchNotiController {
    private final SsuCatchNotiRepository ssuCatchNotiRepository;

    public SsuCatchNotiController(SsuCatchNotiRepository ssuCatchNotiRepository) {
    this.ssuCatchNotiRepository = ssuCatchNotiRepository;
    }

    @GetMapping("/all")
    @Operation(summary = "모든 공지 불러오기")
    public Iterable<SsuCatchNoti> list(){
        return ssuCatchNotiRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
    }
    @GetMapping("/{id}")
    @Operation(summary = "특정 공지 불러오기")
    public Optional<SsuCatchNoti> getById(@PathVariable("id") int id) {
        return ssuCatchNotiRepository.findById(Math.toIntExact(id));
    }
}