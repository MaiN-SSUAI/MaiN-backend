package com.example.MaiN.controller;

import com.example.MaiN.model.ai_noti;
import com.example.MaiN.model.ssucatch_noti;
import com.example.MaiN.repository.ssucatch_noti_Repository;
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
public class ssucatch_noti_Controller {
    private final ssucatch_noti_Repository ssucatch_noti_Repository;

    public ssucatch_noti_Controller(ssucatch_noti_Repository ssucatch_noti_Repository) {
    this.ssucatch_noti_Repository = ssucatch_noti_Repository;
    }

    @GetMapping("/all")
    public Iterable<ssucatch_noti> list(){
        return ssucatch_noti_Repository.findAll(Sort.by(Sort.Direction.DESC, "date"));
    }
    @GetMapping("/{id}")
    public Optional<ssucatch_noti> getById(@PathVariable("id") int id) {
        return ssucatch_noti_Repository.findById(Math.toIntExact(id));
    }
}