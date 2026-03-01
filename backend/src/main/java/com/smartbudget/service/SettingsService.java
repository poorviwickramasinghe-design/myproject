package com.smartbudget.service;

import com.smartbudget.dto.SettingsUpdateDTO;
import com.smartbudget.model.UserSettings;
import com.smartbudget.repository.UserSettingsRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * SettingsService
 *
 * Member 4 + Member 5 – Settings & Notification Preferences
 * Business logic for user settings operations.
 *
 * Screens:
 *   - settings_screen.dart (all toggles)
 *   - notifications_screen.dart (notification prefs)
 */
@Service
public class SettingsService {

    private final UserSettingsRepository settingsRepository;

    public SettingsService(UserSettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    /**
     * Get settings for a user. Returns defaults if not set yet.
     */
    public UserSettings getSettings(String userId) throws ExecutionException, InterruptedException {
        Optional<UserSettings> opt = settingsRepository.findByUserId(userId);
        return opt.orElseGet(() -> buildDefaultSettings(userId));
    }

    /**
     * Update all settings at once (from settings_screen.dart).
     */
    public UserSettings updateSettings(String userId, SettingsUpdateDTO dto)
            throws ExecutionException, InterruptedException {

        UserSettings settings = UserSettings.builder()
                .userId(userId)
                .darkMode(dto.isDarkMode())
                .notificationsEnabled(dto.isNotificationsEnabled())
                .privacyLock(dto.isPrivacyLock())
                .cloudBackup(dto.isCloudBackup())
                .budgetWarningAlerts(dto.isBudgetWarningAlerts())
                .budgetExceededAlerts(dto.isBudgetExceededAlerts())
                .monthlySummaryAlerts(dto.isMonthlySummaryAlerts())
                .paymentReminderAlerts(dto.isPaymentReminderAlerts())
                .goalProgressAlerts(dto.isGoalProgressAlerts())
                .language(dto.getLanguage())
                .fcmToken(dto.getFcmToken())
                .build();

        settingsRepository.save(settings);
        return settings;
    }

    /**
     * Register or update the FCM token for the device.
     * Called by Flutter on every app launch / login.
     */
    public void updateFcmToken(String userId, String fcmToken) throws ExecutionException, InterruptedException {
        java.util.Map<String, Object> fields = new java.util.HashMap<>();
        fields.put("fcmToken", fcmToken);
        settingsRepository.updateFields(userId, fields);
    }

    // ─────────────────────────────────────────────────────────────────────────

    private UserSettings buildDefaultSettings(String userId) {
        return UserSettings.builder()
                .userId(userId)
                .darkMode(false)
                .notificationsEnabled(true)
                .privacyLock(false)
                .cloudBackup(true)
                .budgetWarningAlerts(true)
                .budgetExceededAlerts(true)
                .monthlySummaryAlerts(true)
                .paymentReminderAlerts(true)
                .goalProgressAlerts(true)
                .currency("LKR")
                .language("en")
                .build();
    }
}
