package com.smartbudget.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserSettings Model
 *
 * Member 5 – Settings
 * Member 4 – Notification Preferences
 *
 * Stored in Firestore under: /users/{userId}/settings/preferences
 *
 * Maps directly to the SettingsScreen toggles:
 *   - Dark Mode
 *   - Notifications on/off
 *   - Privacy Lock
 *   - Cloud Backup
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSettings {

    private String userId;                     // Firebase Auth UID

    // === Preferences (SettingsScreen) ===
    private boolean darkMode;                  // Dark mode enabled
    private boolean notificationsEnabled;      // Master notification toggle
    private boolean privacyLock;               // Biometric / PIN lock
    private boolean cloudBackup;               // Auto Firestore backup

    // === Notification Preferences (Member 4) ===
    private boolean budgetWarningAlerts;       // Notify at 80% budget used
    private boolean budgetExceededAlerts;      // Notify when budget exceeded
    private boolean monthlySummaryAlerts;      // Monthly summary on 1st of month
    private boolean paymentReminderAlerts;     // Recurring payment reminders
    private boolean goalProgressAlerts;        // Goal milestone alerts

    // === Currency (also on profile) ===
    private String currency;                   // e.g. "LKR"
    private String language;                   // e.g. "en"

    // === FCM Token ===
    private String fcmToken;                   // Device FCM token for push notifications
}
