package com.smartbudget.controller;

import com.smartbudget.dto.SettingsUpdateDTO;
import com.smartbudget.model.UserSettings;
import com.smartbudget.service.SettingsService;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * SettingsController
 *
 * Member 4 + Member 5 – Settings REST API
 *
 * Base URL: /api/settings
 *
 * Endpoints:
 *   GET  /api/settings             → Get current settings (settings_screen.dart load)
 *   PUT  /api/settings             → Update all settings (Save from SettingsScreen)
 *   POST /api/settings/fcm-token   → Register FCM device token
 */
@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET current user's settings
    // Used by: settings_screen.dart (on screen load to populate toggles)
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<UserSettings> getSettings(Authentication auth) {
        try {
            UserSettings settings = settingsService.getSettings(auth.getName());
            return ResponseEntity.ok(settings);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT update settings
    // Used by: settings_screen.dart (every toggle change or a Save button)
    // Includes notification preference toggles (Member 4)
    // ─────────────────────────────────────────────────────────────────────────
    @PutMapping
    public ResponseEntity<?> updateSettings(
            @Valid @RequestBody SettingsUpdateDTO dto,
            Authentication auth) {
        try {
            UserSettings updated = settingsService.updateSettings(auth.getName(), dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST register FCM token for push notifications
    // Called by Flutter on every app launch (to keep token fresh)
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/fcm-token")
    public ResponseEntity<Map<String, String>> registerFcmToken(
            @RequestBody Map<String, String> body,
            Authentication auth) {
        try {
            String fcmToken = body.get("fcmToken");
            if (fcmToken == null || fcmToken.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "fcmToken is required"));
            }
            settingsService.updateFcmToken(auth.getName(), fcmToken);
            return ResponseEntity.ok(Map.of("status", "FCM token registered"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
