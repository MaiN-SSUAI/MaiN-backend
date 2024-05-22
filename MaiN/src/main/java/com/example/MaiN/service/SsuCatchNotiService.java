package com.example.MaiN.service;

import com.example.MaiN.dto.SsuCatchNotiDto;
import com.example.MaiN.repository.SsuCatchNotiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class SsuCatchNotiService {

    @Autowired
    private final SsuCatchNotiRepository ssuCatchNotiRepository;

    public SsuCatchNotiService(SsuCatchNotiRepository ssuCatchNotiRepository) {
        this.ssuCatchNotiRepository = ssuCatchNotiRepository;
    }

    public List<SsuCatchNotiDto> listAll(){
        return ssuCatchNotiRepository.findAllProjectedBy(Sort.by(Sort.Direction.DESC,"date"));
    }

    public Optional<SsuCatchNotiDto> getNotiByID(int id){
        return ssuCatchNotiRepository.findDtoById(id);
    }
}
