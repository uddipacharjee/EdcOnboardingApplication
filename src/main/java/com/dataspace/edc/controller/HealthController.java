package com.dataspace.edc.controller;

import com.dataspace.edc.api.HealthResponse;
import com.dataspace.edc.service.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HealthController {

    private final HealthService healthService;

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        HealthResponse response = healthService.getHealth();
        return ResponseEntity.ok(response);
    }
}
