package com.smartbudget.dto;

import lombok.Data;

/**
 * SettingsUpdateDTO
 *
 * Member 4 + Member 5 – Notification Preferences & Settings
 * Request body for PUT /api/settings/{userId}
 * Maps to settings_screen.dart toggle switches.
 */
@Data
public class SettingsUpdateDTO {

    // Preferences
    private boolean darkMode;
    private boolean notificationsEnabled;
    private boolean privacyLock;
    private boolean cloudBackup;

    // Notification sub-preferences (Member 4)
    private boolean budgetWarningAlerts;
    private boolean budgetExceededAlerts;
    private boolean monthlySummaryAlerts;
    private boolean paymentReminderAlerts;
    private boolean goalProgressAlerts;

    // Language preference
    private String language;

    // FCM token for push notifications (sent by Flutter on login/app start)
    private String fcmToken;
}
