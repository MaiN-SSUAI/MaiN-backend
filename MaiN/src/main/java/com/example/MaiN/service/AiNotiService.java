package com.example.MaiN.service;

import com.example.MaiN.dto.AiNotiDto;
import com.example.MaiN.repository.AiNotiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AiNotiService {

    @Autowired
    private final AiNotiRepository aiNotiRepository;

    public AiNotiService(AiNotiRepository aiNotiRepository) {
        this.aiNotiRepository = aiNotiRepository;
    }

    public List<AiNotiDto> listAll(){
        return aiNotiRepository.findAllProjectedBy(Sort.by(Sort.Direction.DESC,"startDate"));
    }

    public Optional<AiNotiDto> getNotiById(int id) {
        return aiNotiRepository.findDtoById(id);
    }
}
