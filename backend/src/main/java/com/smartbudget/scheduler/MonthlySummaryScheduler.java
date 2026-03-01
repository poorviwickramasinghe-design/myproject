package com.smartbudget.scheduler;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.smartbudget.dto.NotificationDTO;
import com.smartbudget.model.AppNotification;
import com.smartbudget.model.UserSettings;
import com.smartbudget.repository.UserSettingsRepository;
import com.smartbudget.service.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * MonthlySummaryScheduler
 *
 * Member 4 – Monthly Summary Notifications
 * On the 1st of every month at 8:00 AM, sends each user a summary
 * of their previous month's spending.
 *
 * Trigger: 1st of every month at 08:00
 * Notification Type: MONTHLY_SUMMARY
 * Stored in: /users/{userId}/notifications/{id}
 * Also sent via FCM push to device
 */
@Component
public class MonthlySummaryScheduler {

    private final NotificationService notificationService;
    private final UserSettingsRepository settingsRepository;

    public MonthlySummaryScheduler(NotificationService notificationService,
                                   UserSettingsRepository settingsRepository) {
        this.notificationService = notificationService;
        this.settingsRepository  = settingsRepository;
    }

    /**
     * Monthly summary notification.
     * Cron: 8:00 AM on the 1st of every month  (configurable)
     */
    @Scheduled(cron = "${scheduler.monthly-summary.cron:0 0 8 1 * ?}")
    public void sendMonthlySummaries() {
        // Get the previous month name for the summary title
        LocalDate today = LocalDate.now();
        LocalDate lastMonth = today.minusMonths(1);
        String monthName = lastMonth.getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int year = lastMonth.getYear();

        System.out.println("[MonthlySummaryScheduler] Sending summaries for " + monthName + " " + year);

        try {
            QuerySnapshot usersSnapshot = FirestoreClient.getFirestore()
                    .collection("users")
                    .get()
                    .get();

            List<QueryDocumentSnapshot> users = usersSnapshot.getDocuments();

            for (QueryDocumentSnapshot userDoc : users) {
                String userId = userDoc.getId();
                sendSummaryToUser(userId, monthName, year);
            }

        } catch (Exception e) {
            System.err.println("[MonthlySummaryScheduler] Error: " + e.getMessage());
        }
    }

    /**
     * Build and send the monthly summary notification to a specific user.
     */
    private void sendSummaryToUser(String userId, String monthName, int year) {
        try {
            // Check if user has monthly summary alerts enabled
            Optional<UserSettings> settingsOpt = settingsRepository.findByUserId(userId);
            if (settingsOpt.isPresent() && !settingsOpt.get().isMonthlySummaryAlerts()) {
                return; // User has opted out
            }

            // Build the summary notification
            // (In a real implementation, compute actual totals from /users/{uid}/transactions)
            NotificationDTO dto = new NotificationDTO();
            dto.setUserId(userId);
            dto.setType(AppNotification.TYPE_MONTHLY_SUMMARY);
            dto.setTitle(monthName + " " + year + " Summary");
            dto.setMessage("Your " + monthName + " spending summary is ready. Tap to view your monthly report.");
            dto.setCategory("summary");

            notificationService.sendNotification(dto);

        } catch (Exception e) {
            System.err.println("[MonthlySummaryScheduler] Failed for user " + userId + ": " + e.getMessage());
        }
    }
}
