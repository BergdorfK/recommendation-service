package com.starbank.recommendation_service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/management")
public class InfoController {

    private final BuildProperties buildProperties;

    @Value("${spring.application.name}")
    private String serviceName;

    public InfoController(Optional<BuildProperties> buildProperties) {
        this.buildProperties = buildProperties.orElse(null);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo() {
        String version = (buildProperties != null) ? buildProperties.getVersion() : "unknown";
        Map<String, Object> info = Map.of(
                "name", serviceName,
                "version", version
        );
        return ResponseEntity.ok(info);
    }
}