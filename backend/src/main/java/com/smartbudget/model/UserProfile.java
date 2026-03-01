package com.smartbudget.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * UserProfile Model
 *
 * Member 5 – Profile & Settings
 * Stored in Firestore under: /users/{userId}
 *
 * Security Rule enforced:
 *   allow read, write: if request.auth.uid == userId
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    private String uid;              // Firebase Auth UID (also the Firestore document ID)

    private String fullName;         // User's display name

    private String username;         // @username handle

    private String email;            // Email address (from Firebase Auth)

    private String profileImageUrl;  // Firebase Storage URL for profile photo

    private String currency;         // Selected currency code, e.g. "LKR", "USD"

    private String currencyLabel;    // Full label, e.g. "LKR - Sri Lankan Rupee"

    private LocalDateTime createdAt; // Account creation timestamp

    private LocalDateTime updatedAt; // Last profile update timestamp
}
