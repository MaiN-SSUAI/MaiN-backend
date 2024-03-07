package com.example.MaiN.controller;

import com.example.MaiN.model.ai_noti;
import com.example.MaiN.model.funsys_noti;
import com.example.MaiN.repository.funsys_noti_Repository;
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
public class funsys_noti_Controller {
    private final funsys_noti_Repository funsys_noti_Repository;

    public funsys_noti_Controller(funsys_noti_Repository funsys_noti_Repository) {
        this.funsys_noti_Repository = funsys_noti_Repository;
    }

    @GetMapping("/all")
    public Iterable<funsys_noti> list(){
        return funsys_noti_Repository.findAll(Sort.by(Sort.Direction.DESC, "startDate"));
    }
    @GetMapping("/{id}")
    public Optional<funsys_noti> getById(@PathVariable("id") int id) {
        return funsys_noti_Repository.findById(Math.toIntExact(id));
    }
}
