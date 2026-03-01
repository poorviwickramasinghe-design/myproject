package com.smartbudget.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * ProfileUpdateDTO
 *
 * Member 5 – Profile
 * Request body for PUT /api/profile/{userId}
 * Maps to profile_screen.dart "Save Changes" button.
 */
@Data
public class ProfileUpdateDTO {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 50, message = "Full name must be between 2 and 50 characters")
    private String fullName;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;

    // Optional: URL to profile image uploaded to Firebase Storage
    private String profileImageUrl;
}
