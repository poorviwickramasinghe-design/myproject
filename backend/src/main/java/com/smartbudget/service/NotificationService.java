package com.smartbudget.service;

import com.google.firebase.messaging.*;
import com.smartbudget.dto.NotificationDTO;
import com.smartbudget.model.AppNotification;
import com.smartbudget.repository.NotificationRepository;
import com.smartbudget.repository.UserSettingsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * NotificationService
 *
 * Member 4 – Notifications
 * Core service for:
 *   1. Sending FCM push notifications to the user's device
 *   2. Storing notification history in Firestore
 *   3. Checking notification preferences before sending
 *
 * FCM Docs: https://firebase.google.com/docs/cloud-messaging/send-message
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserSettingsRepository settingsRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserSettingsRepository settingsRepository) {
        this.notificationRepository = notificationRepository;
        this.settingsRepository = settingsRepository;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SEND PUSH NOTIFICATION VIA FCM
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Send a push notification to a specific user via their FCM token.
     * Also stores the notification in Firestore history.
     *
     * @param dto  Notification data (userId, title, message, type)
     */
    public void sendNotification(NotificationDTO dto) throws ExecutionException, InterruptedException, FirebaseMessagingException {
        // 1. Check if user has notifications enabled
        var settingsOpt = settingsRepository.findByUserId(dto.getUserId());
        if (settingsOpt.isPresent() && !settingsOpt.get().isNotificationsEnabled()) {
            // Master notification switch is OFF — skip sending
            return;
        }

        // 2. Get user's FCM device token
        String fcmToken = settingsRepository.getFcmToken(dto.getUserId());
        if (fcmToken != null && !fcmToken.isBlank()) {
            // 3. Build and send the FCM message
            Message fcmMessage = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(
                            Notification.builder()
                                    .setTitle(dto.getTitle())
                                    .setBody(dto.getMessage())
                                    .build()
                    )
                    .putData("type",     dto.getType() != null ? dto.getType() : "")
                    .putData("category", dto.getCategory() != null ? dto.getCategory() : "")
                    .putData("userId",   dto.getUserId())
                    .build();

            String response = FirebaseMessaging.getInstance().send(fcmMessage);
            System.out.println("[FCM] Message sent: " + response);
        }

        // 4. Always store in Firestore notification history (for NotificationsScreen)
        AppNotification notification = AppNotification.builder()
                .userId(dto.getUserId())
                .type(dto.getType())
                .title(dto.getTitle())
                .message(dto.getMessage())
                .category(dto.getCategory())
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // NOTIFICATION HISTORY (for NotificationsScreen)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Get all notifications for a user (shown in notifications_screen.dart).
     */
    public List<AppNotification> getUserNotifications(String userId) throws ExecutionException, InterruptedException {
        return notificationRepository.findByUserId(userId);
    }

    /**
     * Mark a single notification as read.
     */
    public void markAsRead(String userId, String notificationId) throws ExecutionException, InterruptedException {
        notificationRepository.markAsRead(userId, notificationId);
    }

    /**
     * Mark all notifications as read.
     */
    public void markAllAsRead(String userId) throws ExecutionException, InterruptedException {
        notificationRepository.markAllAsRead(userId);
    }

    /**
     * Delete a notification.
     */
    public void deleteNotification(String userId, String notificationId) throws ExecutionException, InterruptedException {
        notificationRepository.delete(userId, notificationId);
    }
}
