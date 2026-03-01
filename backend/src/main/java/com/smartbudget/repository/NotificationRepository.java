package com.smartbudget.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.smartbudget.model.AppNotification;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * NotificationRepository
 *
 * Member 4 – Notifications
 * Handles all Firestore operations for the notifications collection.
 *
 * Firestore path: /users/{userId}/notifications/{notificationId}
 */
@Repository
public class NotificationRepository {

    private static final String USERS_COLLECTION = "users";
    private static final String NOTIFICATIONS_SUB = "notifications";

    private CollectionReference getNotificationsRef(String userId) {
        return FirestoreClient.getFirestore()
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(NOTIFICATIONS_SUB);
    }

    /**
     * Save a new notification to Firestore.
     */
    public AppNotification save(AppNotification notification) throws ExecutionException, InterruptedException {
        String id = notification.getId() != null
                ? notification.getId()
                : UUID.randomUUID().toString();
        notification.setId(id);
        notification.setCreatedAt(LocalDateTime.now());

        Map<String, Object> data = notificationToMap(notification);
        getNotificationsRef(notification.getUserId()).document(id).set(data).get();
        return notification;
    }

    /**
     * Fetch all notifications for a user, ordered by newest first.
     */
    public List<AppNotification> findByUserId(String userId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = getNotificationsRef(userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get();

        List<AppNotification> list = new ArrayList<>();
        for (QueryDocumentSnapshot doc : future.get().getDocuments()) {
            list.add(mapToNotification(doc));
        }
        return list;
    }

    /**
     * Mark a specific notification as read.
     */
    public void markAsRead(String userId, String notificationId) throws ExecutionException, InterruptedException {
        getNotificationsRef(userId).document(notificationId)
                .update("isRead", true)
                .get();
    }

    /**
     * Mark all notifications for a user as read.
     */
    public void markAllAsRead(String userId) throws ExecutionException, InterruptedException {
        QuerySnapshot snapshot = getNotificationsRef(userId).get().get();
        WriteBatch batch = FirestoreClient.getFirestore().batch();
        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
            batch.update(doc.getReference(), "isRead", true);
        }
        batch.commit().get();
    }

    /**
     * Delete a single notification.
     */
    public void delete(String userId, String notificationId) throws ExecutionException, InterruptedException {
        getNotificationsRef(userId).document(notificationId).delete().get();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Map<String, Object> notificationToMap(AppNotification n) {
        Map<String, Object> map = new HashMap<>();
        map.put("id",        n.getId());
        map.put("userId",    n.getUserId());
        map.put("type",      n.getType());
        map.put("title",     n.getTitle());
        map.put("message",   n.getMessage());
        map.put("isRead",    n.isRead());
        map.put("category",  n.getCategory());
        map.put("createdAt", n.getCreatedAt() != null ? n.getCreatedAt().toString() : null);
        return map;
    }

    private AppNotification mapToNotification(QueryDocumentSnapshot doc) {
        return AppNotification.builder()
                .id(doc.getString("id"))
                .userId(doc.getString("userId"))
                .type(doc.getString("type"))
                .title(doc.getString("title"))
                .message(doc.getString("message"))
                .isRead(Boolean.TRUE.equals(doc.getBoolean("isRead")))
                .category(doc.getString("category"))
                .build();
    }
}
