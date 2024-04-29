package com.example.MaiN.controller;

import com.example.MaiN.entity.FunsysNoti;
import com.example.MaiN.repository.FunsysNotiRepository;
import lombok.Getter;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Getter
@RestController
@RequestMapping(value = "/funsys_noti")
public class FunsysNotiController {
    private final FunsysNotiRepository funsysNotiRepository;

    public FunsysNotiController(FunsysNotiRepository funsysNotiRepository) {
        this.funsysNotiRepository = funsysNotiRepository;
    }

    @GetMapping("/all")
    public Iterable<FunsysNoti> list(){
        return funsysNotiRepository.findAll(Sort.by(Sort.Direction.DESC, "startDate"));
    }
    @GetMapping("/{id}")
    public Optional<FunsysNoti> getById(@PathVariable("id") int id) {
        return funsysNotiRepository.findById(Math.toIntExact(id));
    }
}
