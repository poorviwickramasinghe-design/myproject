package com.smartbudget.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * HelpMessageDTO
 *
 * Member 5 – Help & Support
 * Request body for POST /api/help
 * Maps to help_support_screen.dart submit action.
 */
@Data
public class HelpMessageDTO {

    @NotBlank(message = "Subject is required")
    @Size(max = 100, message = "Subject cannot exceed 100 characters")
    private String subject;

    @NotBlank(message = "Message is required")
    @Size(min = 10, max = 1000, message = "Message must be between 10 and 1000 characters")
    private String message;
}
