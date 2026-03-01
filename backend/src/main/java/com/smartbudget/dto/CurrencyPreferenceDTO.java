package com.smartbudget.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * CurrencyPreferenceDTO
 *
 * Member 5 – Currency Preferences
 * Request body for PUT /api/profile/{userId}/currency
 * Maps to select_currency_screen.dart "Confirm & Get Started" button.
 */
@Data
public class CurrencyPreferenceDTO {

    @NotBlank(message = "Currency code is required")
    private String currency;       // e.g. "LKR", "USD", "EUR"

    @NotBlank(message = "Currency label is required")
    private String currencyLabel;  // e.g. "LKR - Sri Lankan Rupee"
}
