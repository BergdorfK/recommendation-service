package com.starbank.recommendation_service.management;

import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/management")
public class InfoController {

    private final BuildProperties build;

    public InfoController(BuildProperties build) {
        this.build = build;
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        String name = (build != null ? build.getName() : "recommendation-service");
        String version = (build != null ? build.getVersion() : "dev");
        return ResponseEntity.ok(Map.of(
                "name", name,
                "version", version
        ));
    }
}
