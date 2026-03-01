package com.smartbudget.service;

import com.google.cloud.firestore.CollectionReference;
import com.google.firebase.cloud.FirestoreClient;
import com.smartbudget.dto.HelpMessageDTO;
import com.smartbudget.model.HelpSupportMessage;
import com.smartbudget.util.ValidationUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * HelpSupportService
 *
 * Member 5 – Help & Support
 * Saves help/support messages from users to Firestore.
 *
 * Firestore path: /helpMessages/{messageId}
 * Screen: help_support_screen.dart
 */
@Service
public class HelpSupportService {

    private static final String HELP_COLLECTION = "helpMessages";

    /**
     * Submit a new help/support message.
     * Validates content before saving to Firestore.
     */
    public HelpSupportMessage submitMessage(String userId, HelpMessageDTO dto)
            throws ExecutionException, InterruptedException {

        ValidationUtil.validateHelpMessage(dto);

        HelpSupportMessage msg = HelpSupportMessage.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .subject(dto.getSubject().trim())
                .message(dto.getMessage().trim())
                .status(HelpSupportMessage.STATUS_OPEN)
                .sentAt(LocalDateTime.now())
                .build();

        CollectionReference col = FirestoreClient.getFirestore().collection(HELP_COLLECTION);
        col.document(msg.getId()).set(messageToMap(msg)).get();

        return msg;
    }

    // ─────────────────────────────────────────────────────────────────────────

    private Map<String, Object> messageToMap(HelpSupportMessage m) {
        Map<String, Object> map = new HashMap<>();
        map.put("id",      m.getId());
        map.put("userId",  m.getUserId());
        map.put("subject", m.getSubject());
        map.put("message", m.getMessage());
        map.put("status",  m.getStatus());
        map.put("sentAt",  m.getSentAt().toString());
        return map;
    }
}
