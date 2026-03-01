package com.smartbudget.controller;

import com.smartbudget.dto.HelpMessageDTO;
import com.smartbudget.model.HelpSupportMessage;
import com.smartbudget.service.HelpSupportService;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * HelpSupportController
 *
 * Member 5 – Help & Support REST API
 *
 * Base URL: /api/help
 *
 * Endpoints:
 *   POST /api/help  → Submit a help/support message
 *
 * Screen: help_support_screen.dart
 */
@RestController
@RequestMapping("/api/help")
public class HelpSupportController {

    private final HelpSupportService helpSupportService;

    public HelpSupportController(HelpSupportService helpSupportService) {
        this.helpSupportService = helpSupportService;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST submit help message
    // Used by: help_support_screen.dart submit button
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> submitHelpMessage(
            @Valid @RequestBody HelpMessageDTO dto,
            Authentication auth) {
        try {
            HelpSupportMessage saved = helpSupportService.submitMessage(auth.getName(), dto);
            return ResponseEntity.ok(Map.of(
                    "status",    "Message submitted successfully",
                    "messageId", saved.getId()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
