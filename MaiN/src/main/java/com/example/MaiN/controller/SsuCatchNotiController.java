package com.example.MaiN.controller;

import com.example.MaiN.entity.SsuCatchNoti;
import com.example.MaiN.repository.SsuCatchNotiRepository;
import lombok.Getter;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Getter
@RestController
@RequestMapping(value = "/ssucatch_noti")
public class SsuCatchNotiController {
    private final SsuCatchNotiRepository ssuCatchNotiRepository;

    public SsuCatchNotiController(SsuCatchNotiRepository ssuCatchNotiRepository) {
    this.ssuCatchNotiRepository = ssuCatchNotiRepository;
    }

    @GetMapping("/all")
    public Iterable<SsuCatchNoti> list(){
        return ssuCatchNotiRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
    }
    @GetMapping("/{id}")
    public Optional<SsuCatchNoti> getById(@PathVariable("id") int id) {
        return ssuCatchNotiRepository.findById(Math.toIntExact(id));
    }
}