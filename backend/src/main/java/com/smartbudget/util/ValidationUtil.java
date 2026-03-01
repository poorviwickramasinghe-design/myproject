package com.smartbudget.util;

import com.smartbudget.dto.HelpMessageDTO;
import com.smartbudget.dto.ProfileUpdateDTO;

/**
 * ValidationUtil
 *
 * Member 5 – Data Validation
 * Custom business-rule validation beyond @Valid annotations.
 * Throws IllegalArgumentException with a descriptive message on failure.
 */
public class ValidationUtil {

    private ValidationUtil() {}

    /**
     * Validate ProfileUpdateDTO business rules.
     * - Username must not contain spaces
     * - Username must be alphanumeric with underscores only
     * - Full name must not be empty after trim
     */
    public static void validateProfileUpdate(ProfileUpdateDTO dto) {
        if (dto.getFullName() == null || dto.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty.");
        }
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        String username = dto.getUsername().trim();
        if (!username.matches("^[a-zA-Z0-9_]{3,30}$")) {
            throw new IllegalArgumentException(
                "Username must be 3-30 characters and contain only letters, numbers, and underscores.");
        }
    }

    /**
     * Validate HelpMessageDTO business rules.
     * - Message must not be blank or just whitespace
     * - Subject must not be blank
     */
    public static void validateHelpMessage(HelpMessageDTO dto) {
        if (dto.getSubject() == null || dto.getSubject().trim().isEmpty()) {
            throw new IllegalArgumentException("Subject cannot be empty.");
        }
        if (dto.getMessage() == null || dto.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty.");
        }
        if (dto.getMessage().trim().length() < 10) {
            throw new IllegalArgumentException("Message must be at least 10 characters.");
        }
    }
}
