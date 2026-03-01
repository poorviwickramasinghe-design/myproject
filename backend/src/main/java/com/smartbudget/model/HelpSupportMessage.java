package com.smartbudget.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * HelpSupportMessage Model
 *
 * Member 5 – Help & Support
 * Stored in Firestore under: /helpMessages/{messageId}
 *
 * Users submit messages from help_support_screen.dart.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HelpSupportMessage {

    private String id;            // Firestore document ID

    private String userId;        // Firebase Auth UID (sender)

    private String subject;       // Message subject / topic

    private String message;       // The support message body

    private String status;        // "OPEN", "IN_PROGRESS", "RESOLVED"

    private LocalDateTime sentAt; // When the message was submitted

    public static final String STATUS_OPEN        = "OPEN";
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_RESOLVED    = "RESOLVED";
}
