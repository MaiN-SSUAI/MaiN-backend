package com.example.MaiN.service;

import com.example.MaiN.dto.VersionDto;
import com.example.MaiN.entity.Version;
import com.example.MaiN.repository.VersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VersionService {

    private final VersionRepository versionRepository;

    @Autowired
    public VersionService(VersionRepository versionRepository) {
        this.versionRepository = versionRepository;
    }

    public VersionDto getIosVersion() {
        Version version = versionRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Version not found!"));
        return new VersionDto(version.getLatestIos(), version.getPresentIos());
    }

    public VersionDto getAndroidVersion() {
        Version version = versionRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Version not found!"));
        return new VersionDto(version.getLatestAndroid(), version.getPresentAndroid());
    }
}
