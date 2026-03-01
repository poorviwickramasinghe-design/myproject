package com.smartbudget.dto;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * BudgetAlertDTO
 *
 * Member 4 – Budget Alert Triggers
 * Sent by Flutter when budget spending is updated.
 * POST /api/notifications/check-budget
 *
 * The backend evaluates the percentage and fires:
 *   - BUDGET_WARNING  if spentPercent >= 80%
 *   - BUDGET_EXCEEDED if spentPercent >= 100%
 */
@Data
public class BudgetAlertDTO {

    @NotBlank(message = "userId is required")
    private String userId;

    @NotBlank(message = "categoryName is required")
    private String categoryName;  // e.g. "Food", "Transport"

    @DecimalMin(value = "0.0")
    private double budgetLimit;   // The set budget limit

    @DecimalMin(value = "0.0")
    private double amountSpent;   // Amount spent so far

    // Computed by backend: (amountSpent / budgetLimit) * 100
    // Included here so Flutter can also pass it directly
    private double spentPercent;
}
