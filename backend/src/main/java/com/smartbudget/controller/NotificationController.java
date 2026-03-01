package com.smartbudget.controller;

import com.smartbudget.dto.BudgetAlertDTO;
import com.smartbudget.dto.NotificationDTO;
import com.smartbudget.model.AppNotification;
import com.smartbudget.service.BudgetAlertService;
import com.smartbudget.service.NotificationService;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * NotificationController
 *
 * Member 4 – Notifications REST API
 *
 * Base URL: /api/notifications
 *
 * Endpoints:
 *   GET    /api/notifications              → Get all notifications (NotificationsScreen)
 *   POST   /api/notifications/send         → Send a push notification
 *   POST   /api/notifications/check-budget → Trigger budget alert check
 *   PUT    /api/notifications/{id}/read    → Mark notification as read
 *   PUT    /api/notifications/read-all     → Mark all as read
 *   DELETE /api/notifications/{id}         → Delete a notification
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final BudgetAlertService budgetAlertService;

    public NotificationController(NotificationService notificationService,
                                  BudgetAlertService budgetAlertService) {
        this.notificationService = notificationService;
        this.budgetAlertService  = budgetAlertService;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET all notifications for the authenticated user
    // Used by: notifications_screen.dart (ListView)
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<AppNotification>> getNotifications(Authentication auth) {
        try {
            String userId = auth.getName();
            List<AppNotification> notifications = notificationService.getUserNotifications(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST send a push notification (FCM + store in Firestore)
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendNotification(
            @Valid @RequestBody NotificationDTO dto,
            Authentication auth) {
        try {
            // Security: only allow sending to the authenticated user's own account
            dto.setUserId(auth.getName());
            notificationService.sendNotification(dto);
            return ResponseEntity.ok(Map.of("status", "Notification sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST check budget and fire alert if threshold reached
    // Trigger: Called when user adds an expense (dashboard_screen.dart)
    // Thresholds: 80% → WARNING, 100% → EXCEEDED
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/check-budget")
    public ResponseEntity<Map<String, String>> checkBudget(
            @Valid @RequestBody BudgetAlertDTO dto,
            Authentication auth) {
        try {
            dto.setUserId(auth.getName());
            budgetAlertService.checkAndAlert(dto);
            return ResponseEntity.ok(Map.of("status", "Budget check completed"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT mark a single notification as read
    // ─────────────────────────────────────────────────────────────────────────
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Map<String, String>> markAsRead(
            @PathVariable String notificationId,
            Authentication auth) {
        try {
            notificationService.markAsRead(auth.getName(), notificationId);
            return ResponseEntity.ok(Map.of("status", "Marked as read"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT mark all notifications as read
    // ─────────────────────────────────────────────────────────────────────────
    @PutMapping("/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead(Authentication auth) {
        try {
            notificationService.markAllAsRead(auth.getName());
            return ResponseEntity.ok(Map.of("status", "All notifications marked as read"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE a notification
    // ─────────────────────────────────────────────────────────────────────────
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Map<String, String>> deleteNotification(
            @PathVariable String notificationId,
            Authentication auth) {
        try {
            notificationService.deleteNotification(auth.getName(), notificationId);
            return ResponseEntity.ok(Map.of("status", "Notification deleted"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
