package com.smartbudget.controller;

import com.smartbudget.dto.CurrencyPreferenceDTO;
import com.smartbudget.dto.ProfileUpdateDTO;
import com.smartbudget.model.UserProfile;
import com.smartbudget.service.ProfileService;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ProfileController
 *
 * Member 5 – Profile REST API
 *
 * Base URL: /api/profile
 *
 * Endpoints:
 *   GET  /api/profile              → Get own profile (profile_screen.dart load)
 *   PUT  /api/profile              → Update name, username, image (Save Changes)
 *   PUT  /api/profile/currency     → Update currency preference (select_currency_screen.dart)
 *   POST /api/profile/init         → Initialize profile after signup
 *
 * Security: All routes use the authenticated user's uid (no userId in path needed).
 * This enforces: allow read, write: if request.auth.uid == userId
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET current user's profile
    // Used by: profile_screen.dart (on load to populate fields)
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<UserProfile> getProfile(Authentication auth) {
        try {
            UserProfile profile = profileService.getProfile(auth.getName());
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT update profile (name, username, profile image)
    // Used by: profile_screen.dart "Save Changes" button
    // ─────────────────────────────────────────────────────────────────────────
    @PutMapping
    public ResponseEntity<?> updateProfile(
            @Valid @RequestBody ProfileUpdateDTO dto,
            Authentication auth) {
        try {
            UserProfile updated = profileService.updateProfile(auth.getName(), dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT update currency preference
    // Used by: select_currency_screen.dart "Confirm & Get Started" button
    // ─────────────────────────────────────────────────────────────────────────
    @PutMapping("/currency")
    public ResponseEntity<Map<String, String>> updateCurrency(
            @Valid @RequestBody CurrencyPreferenceDTO dto,
            Authentication auth) {
        try {
            profileService.updateCurrency(auth.getName(), dto);
            return ResponseEntity.ok(Map.of("status", "Currency updated to " + dto.getCurrency()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST initialize profile after first signup
    // Called once after Firebase Auth registration
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/init")
    public ResponseEntity<UserProfile> initProfile(
            @RequestBody Map<String, String> body,
            Authentication auth) {
        try {
            String email = body.getOrDefault("email", "");
            UserProfile profile = profileService.createInitialProfile(auth.getName(), email);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
