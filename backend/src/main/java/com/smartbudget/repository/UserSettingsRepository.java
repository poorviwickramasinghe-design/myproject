package com.smartbudget.repository;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.smartbudget.model.UserSettings;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * UserSettingsRepository
 *
 * Member 4 + Member 5 – Settings & Notification Preferences
 * Firestore path: /users/{userId}/settings/preferences
 */
@Repository
public class UserSettingsRepository {

    private static final String USERS_COLLECTION   = "users";
    private static final String SETTINGS_SUB       = "settings";
    private static final String PREFERENCES_DOC    = "preferences";

    private DocumentReference getSettingsRef(String userId) {
        return FirestoreClient.getFirestore()
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(SETTINGS_SUB)
                .document(PREFERENCES_DOC);
    }

    /**
     * Get settings for a user. Returns defaults if not found.
     */
    public Optional<UserSettings> findByUserId(String userId) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = getSettingsRef(userId).get().get();
        if (!doc.exists()) return Optional.empty();
        return Optional.of(mapToSettings(doc, userId));
    }

    /**
     * Save (upsert) user settings.
     */
    public void save(UserSettings settings) throws ExecutionException, InterruptedException {
        getSettingsRef(settings.getUserId()).set(settingsToMap(settings)).get();
    }

    /**
     * Update specific setting fields only.
     */
    public void updateFields(String userId, Map<String, Object> fields) throws ExecutionException, InterruptedException {
        getSettingsRef(userId).update(fields).get();
    }

    /**
     * Get the FCM token for push notifications.
     */
    public String getFcmToken(String userId) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = getSettingsRef(userId).get().get();
        if (!doc.exists()) return null;
        return doc.getString("fcmToken");
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Map<String, Object> settingsToMap(UserSettings s) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId",                s.getUserId());
        map.put("darkMode",              s.isDarkMode());
        map.put("notificationsEnabled",  s.isNotificationsEnabled());
        map.put("privacyLock",           s.isPrivacyLock());
        map.put("cloudBackup",           s.isCloudBackup());
        map.put("budgetWarningAlerts",   s.isBudgetWarningAlerts());
        map.put("budgetExceededAlerts",  s.isBudgetExceededAlerts());
        map.put("monthlySummaryAlerts",  s.isMonthlySummaryAlerts());
        map.put("paymentReminderAlerts", s.isPaymentReminderAlerts());
        map.put("goalProgressAlerts",    s.isGoalProgressAlerts());
        map.put("currency",              s.getCurrency());
        map.put("language",              s.getLanguage());
        map.put("fcmToken",              s.getFcmToken());
        return map;
    }

    private UserSettings mapToSettings(DocumentSnapshot doc, String userId) {
        return UserSettings.builder()
                .userId(userId)
                .darkMode(Boolean.TRUE.equals(doc.getBoolean("darkMode")))
                .notificationsEnabled(Boolean.TRUE.equals(doc.getBoolean("notificationsEnabled")))
                .privacyLock(Boolean.TRUE.equals(doc.getBoolean("privacyLock")))
                .cloudBackup(Boolean.TRUE.equals(doc.getBoolean("cloudBackup")))
                .budgetWarningAlerts(Boolean.TRUE.equals(doc.getBoolean("budgetWarningAlerts")))
                .budgetExceededAlerts(Boolean.TRUE.equals(doc.getBoolean("budgetExceededAlerts")))
                .monthlySummaryAlerts(Boolean.TRUE.equals(doc.getBoolean("monthlySummaryAlerts")))
                .paymentReminderAlerts(Boolean.TRUE.equals(doc.getBoolean("paymentReminderAlerts")))
                .goalProgressAlerts(Boolean.TRUE.equals(doc.getBoolean("goalProgressAlerts")))
                .currency(doc.getString("currency"))
                .language(doc.getString("language"))
                .fcmToken(doc.getString("fcmToken"))
                .build();
    }
}
