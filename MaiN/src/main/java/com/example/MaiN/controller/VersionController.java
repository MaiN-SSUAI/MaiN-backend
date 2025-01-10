package com.example.MaiN.controller;

import com.example.MaiN.dto.VersionDto;
import com.example.MaiN.service.VersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/version")
public class VersionController {

    private final VersionService versionService;

    @Autowired
    public VersionController(VersionService versionService) {
        this.versionService = versionService;
    }

    @GetMapping("/ios")
    public VersionDto getIosVersion() {
        return versionService.getIosVersion();
    }

    @GetMapping("/android")
    public VersionDto getAndroidVersion() {
        return versionService.getAndroidVersion();
    }
}
