package com.smartbudget.repository;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.smartbudget.model.UserProfile;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * UserProfileRepository
 *
 * Member 5 – Profile & Security
 * Handles Firestore read/write for user profiles.
 *
 * Firestore path: /users/{userId}
 *
 * Security: Only the authenticated user (matching uid) can access their own document.
 * This is enforced by FirebaseTokenFilter + SecurityConfig at the API level,
 * and by Firestore Security Rules on the database level.
 */
@Repository
public class UserProfileRepository {

    private static final String USERS_COLLECTION = "users";

    private DocumentReference getUserRef(String userId) {
        return FirestoreClient.getFirestore()
                .collection(USERS_COLLECTION)
                .document(userId);
    }

    /**
     * Get user profile by Firebase UID.
     */
    public Optional<UserProfile> findById(String userId) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = getUserRef(userId).get().get();
        if (!doc.exists()) return Optional.empty();
        return Optional.of(mapToProfile(doc));
    }

    /**
     * Create or update a user profile (upsert).
     */
    public UserProfile save(UserProfile profile) throws ExecutionException, InterruptedException {
        profile.setUpdatedAt(LocalDateTime.now());
        if (profile.getCreatedAt() == null) {
            profile.setCreatedAt(LocalDateTime.now());
        }
        getUserRef(profile.getUid()).set(profileToMap(profile)).get();
        return profile;
    }

    /**
     * Partial update – only the provided fields (profile name, username, image).
     */
    public void updateFields(String userId, Map<String, Object> fields) throws ExecutionException, InterruptedException {
        fields.put("updatedAt", LocalDateTime.now().toString());
        getUserRef(userId).update(fields).get();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Map<String, Object> profileToMap(UserProfile p) {
        Map<String, Object> map = new HashMap<>();
        map.put("uid",             p.getUid());
        map.put("fullName",        p.getFullName());
        map.put("username",        p.getUsername());
        map.put("email",           p.getEmail());
        map.put("profileImageUrl", p.getProfileImageUrl());
        map.put("currency",        p.getCurrency());
        map.put("currencyLabel",   p.getCurrencyLabel());
        map.put("createdAt",       p.getCreatedAt() != null ? p.getCreatedAt().toString() : null);
        map.put("updatedAt",       p.getUpdatedAt() != null ? p.getUpdatedAt().toString() : null);
        return map;
    }

    private UserProfile mapToProfile(DocumentSnapshot doc) {
        return UserProfile.builder()
                .uid(doc.getString("uid"))
                .fullName(doc.getString("fullName"))
                .username(doc.getString("username"))
                .email(doc.getString("email"))
                .profileImageUrl(doc.getString("profileImageUrl"))
                .currency(doc.getString("currency"))
                .currencyLabel(doc.getString("currencyLabel"))
                .build();
    }
}
