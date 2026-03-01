package com.smartbudget.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * HealthController
 *
 * Public endpoint to verify the backend is running.
 * No authentication required.
 *
 * GET /api/health → { "status": "UP", "service": "Smart Budget Backend" }
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status",  "UP",
                "service", "Smart Student Budget Tracker Backend",
                "version", "1.0.0"
        ));
    }
}
