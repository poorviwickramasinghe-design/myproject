package com.smartbudget.service;

import com.smartbudget.dto.BudgetAlertDTO;
import com.smartbudget.dto.NotificationDTO;
import com.smartbudget.model.AppNotification;
import com.smartbudget.model.UserSettings;
import com.smartbudget.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * BudgetAlertService
 *
 * Member 4 – Budget Alert Notifications
 * Evaluates budget spending and triggers push notifications when:
 *   - Budget reaches 80%  → BUDGET_WARNING notification
 *   - Budget exceeded 100% → BUDGET_EXCEEDED notification
 *
 * Called by:
 *   - NotificationController (POST /api/notifications/check-budget)
 *   - BudgetAlertScheduler (scheduled hourly check)
 */
@Service
public class BudgetAlertService {

    @Value("${budget.alert.warning-percent:80}")
    private double warningPercent;

    @Value("${budget.alert.exceeded-percent:100}")
    private double exceededPercent;

    private final NotificationService notificationService;
    private final UserSettingsRepository settingsRepository;

    public BudgetAlertService(NotificationService notificationService,
                              UserSettingsRepository settingsRepository) {
        this.notificationService = notificationService;
        this.settingsRepository  = settingsRepository;
    }

    /**
     * Evaluates the budget and sends the appropriate alert.
     *
     * @param dto  BudgetAlertDTO containing userId, categoryName, budgetLimit, amountSpent
     */
    public void checkAndAlert(BudgetAlertDTO dto) throws Exception {
        double percent = (dto.getBudgetLimit() > 0)
                ? (dto.getAmountSpent() / dto.getBudgetLimit()) * 100.0
                : 0.0;

        // Check if user has this alert type enabled
        Optional<UserSettings> settingsOpt = settingsRepository.findByUserId(dto.getUserId());

        if (percent >= exceededPercent) {
            // Budget EXCEEDED
            if (settingsOpt.isEmpty() || settingsOpt.get().isBudgetExceededAlerts()) {
                sendBudgetAlert(dto, AppNotification.TYPE_BUDGET_EXCEEDED,
                        "Budget Exceeded!",
                        "You have exceeded your " + dto.getCategoryName() + " budget.");
            }

        } else if (percent >= warningPercent) {
            // Budget WARNING (80%)
            if (settingsOpt.isEmpty() || settingsOpt.get().isBudgetWarningAlerts()) {
                int roundedPercent = (int) Math.round(percent);
                sendBudgetAlert(dto, AppNotification.TYPE_BUDGET_WARNING,
                        "Budget Alert",
                        "You have used " + roundedPercent + "% of your " + dto.getCategoryName() + " budget.");
            }
        }
        // Below 80% – no notification needed
    }

    // ─────────────────────────────────────────────────────────────────────────

    private void sendBudgetAlert(BudgetAlertDTO dto, String type, String title, String message) throws Exception {
        NotificationDTO notif = new NotificationDTO();
        notif.setUserId(dto.getUserId());
        notif.setType(type);
        notif.setTitle(title);
        notif.setMessage(message);
        notif.setCategory("budget");
        notificationService.sendNotification(notif);
    }
}
