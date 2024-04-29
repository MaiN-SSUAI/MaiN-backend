package com.example.MaiN.controller;

import com.example.MaiN.entity.AiNoti;
import com.example.MaiN.service.AiNotiFavorService;
import com.example.MaiN.repository.AiNotiRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;
@Getter
@RestController
@RequestMapping(value = "/ai_noti")
public class AiNotiController {

    @Autowired
    private final AiNotiFavorService aiNotiFavorService;

    private final AiNotiRepository aiNotiRepository;

    @Autowired
    public AiNotiController(AiNotiFavorService aiNotiFavorService, AiNotiRepository aiNotiRepository) {
        this.aiNotiFavorService = aiNotiFavorService;
        this.aiNotiRepository = aiNotiRepository;
    }

    @GetMapping("/all")
    public Iterable<AiNoti> list() {
        return aiNotiRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
    }
    @GetMapping("/{id}")
    public Optional<AiNoti> getById(@PathVariable int id) {
        return aiNotiRepository.findById(id);
    }
}