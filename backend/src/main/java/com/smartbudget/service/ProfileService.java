package com.smartbudget.service;

import com.smartbudget.dto.CurrencyPreferenceDTO;
import com.smartbudget.dto.ProfileUpdateDTO;
import com.smartbudget.model.UserProfile;
import com.smartbudget.repository.UserProfileRepository;
import com.smartbudget.util.ValidationUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * ProfileService
 *
 * Member 5 – Profile Management
 * Business logic for user profile operations.
 *
 * Screens:
 *   - profile_screen.dart  (view + edit profile)
 *   - select_currency_screen.dart (currency preference)
 *
 * Security: All operations validate that the requesting uid matches the target userId
 */
@Service
public class ProfileService {

    private final UserProfileRepository profileRepository;

    public ProfileService(UserProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * Get user profile. Creates a default profile if none exists.
     */
    public UserProfile getProfile(String userId) throws ExecutionException, InterruptedException {
        Optional<UserProfile> opt = profileRepository.findById(userId);
        return opt.orElseGet(() -> UserProfile.builder()
                .uid(userId)
                .fullName("New User")
                .username("user_" + userId.substring(0, 6))
                .currency("LKR")
                .currencyLabel("LKR - Sri Lankan Rupee")
                .build());
    }

    /**
     * Update profile name, username, and profile image URL.
     * Validates input before persisting to Firestore.
     */
    public UserProfile updateProfile(String userId, ProfileUpdateDTO dto)
            throws ExecutionException, InterruptedException {

        ValidationUtil.validateProfileUpdate(dto);

        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName",        dto.getFullName().trim());
        updates.put("username",        dto.getUsername().trim().toLowerCase());
        if (dto.getProfileImageUrl() != null && !dto.getProfileImageUrl().isBlank()) {
            updates.put("profileImageUrl", dto.getProfileImageUrl().trim());
        }

        profileRepository.updateFields(userId, updates);
        return getProfile(userId);
    }

    /**
     * Update currency preference.
     * Called from select_currency_screen.dart.
     */
    public void updateCurrency(String userId, CurrencyPreferenceDTO dto)
            throws ExecutionException, InterruptedException {

        Map<String, Object> updates = new HashMap<>();
        updates.put("currency",      dto.getCurrency().toUpperCase().trim());
        updates.put("currencyLabel", dto.getCurrencyLabel().trim());

        profileRepository.updateFields(userId, updates);
    }

    /**
     * Initialize a user profile after signup (called once on first login).
     */
    public UserProfile createInitialProfile(String userId, String email)
            throws ExecutionException, InterruptedException {

        UserProfile profile = UserProfile.builder()
                .uid(userId)
                .email(email)
                .fullName("User")
                .username("user_" + userId.substring(0, 6))
                .currency("LKR")
                .currencyLabel("LKR - Sri Lankan Rupee")
                .profileImageUrl(null)
                .build();

        return profileRepository.save(profile);
    }
}
