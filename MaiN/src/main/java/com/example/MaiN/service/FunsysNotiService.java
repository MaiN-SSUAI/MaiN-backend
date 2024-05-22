package com.example.MaiN.service;

import com.example.MaiN.dto.FunsysNotiDto;
import com.example.MaiN.entity.FunsysNoti;
import com.example.MaiN.repository.FunsysNotiRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FunsysNotiService {
    private final FunsysNotiRepository funsysNotiRepository;

    public FunsysNotiService(FunsysNotiRepository funsysNotiRepository) {
        this.funsysNotiRepository = funsysNotiRepository;
    }

    public List<FunsysNotiDto> listAll() {
        return funsysNotiRepository.findAllProjectedBy(Sort.by(Sort.Direction.DESC, "startDate"));
    }

    public Optional<FunsysNotiDto> getNotiById(int id) {
        return funsysNotiRepository.findDtoById(id);
    }
}