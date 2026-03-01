package com.smartbudget.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * NotificationDTO
 *
 * Member 4 – Notifications
 * Used to send push notification requests to the backend.
 * Flutter → POST /api/notifications/send
 */
@Data
public class NotificationDTO {

    @NotBlank(message = "userId is required")
    private String userId;

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "message is required")
    private String message;

    // Type of notification (e.g. BUDGET_WARNING, MONTHLY_SUMMARY)
    private String type;

    // Category for grouping (e.g. "budget", "payment", "goal")
    private String category;
}
