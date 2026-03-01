package com.smartbudget.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AppNotification Model
 *
 * Member 4 – Notifications
 * Represents a notification stored in Firestore under:
 *   /users/{userId}/notifications/{notificationId}
 *
 * Types:
 *   BUDGET_WARNING   – budget reached 80%
 *   BUDGET_EXCEEDED  – budget exceeded 100%
 *   MONTHLY_SUMMARY  – monthly spending report
 *   PAYMENT_REMINDER – recurring payment due
 *   GOAL_PROGRESS    – savings goal milestone
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppNotification {

    private String id;             // Firestore document ID

    private String userId;         // Firebase Auth UID (owner)

    private String type;           // e.g. "BUDGET_WARNING", "BUDGET_EXCEEDED", "MONTHLY_SUMMARY"

    private String title;          // Notification title shown to user

    private String message;        // Notification body message

    private boolean isRead;        // Whether user has read it

    private String category;       // e.g. "budget", "payment", "goal"

    private LocalDateTime createdAt;  // When the notification was created

    /**
     * Notification type constants
     */
    public static final String TYPE_BUDGET_WARNING  = "BUDGET_WARNING";
    public static final String TYPE_BUDGET_EXCEEDED = "BUDGET_EXCEEDED";
    public static final String TYPE_MONTHLY_SUMMARY = "MONTHLY_SUMMARY";
    public static final String TYPE_PAYMENT_REMINDER = "PAYMENT_REMINDER";
    public static final String TYPE_GOAL_PROGRESS   = "GOAL_PROGRESS";
}
