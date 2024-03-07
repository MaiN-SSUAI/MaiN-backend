package com.example.MaiN.controller;

import com.example.MaiN.service.AiNotiFavoritesService;
import com.example.MaiN.model.ai_noti;
import com.example.MaiN.repository.ai_noti_Repository;
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
public class ai_noti_Controller {

    @Autowired
    private AiNotiFavoritesService aiNotiFavoritesService;

    private final ai_noti_Repository ai_noti_Repository;

    @Autowired
    public ai_noti_Controller(AiNotiFavoritesService aiNotiFavoritesService, ai_noti_Repository ai_noti_Repository) {
        this.aiNotiFavoritesService = aiNotiFavoritesService;
        this.ai_noti_Repository = ai_noti_Repository;
    }

    @GetMapping("/all")
    public Iterable<ai_noti> list() {
        return ai_noti_Repository.findAll(Sort.by(Sort.Direction.DESC, "date"));
    }
    @GetMapping("/{id}")
    public Optional<ai_noti> getById(@PathVariable int id) {
        return ai_noti_Repository.findById(id);
    }
}