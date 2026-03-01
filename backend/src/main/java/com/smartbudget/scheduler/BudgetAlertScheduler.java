package com.smartbudget.scheduler;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.smartbudget.dto.BudgetAlertDTO;
import com.smartbudget.service.BudgetAlertService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * BudgetAlertScheduler
 *
 * Member 4 – Scheduled Budget Monitoring
 * Runs an automated check every hour to detect users who have
 * reached 80% or exceeded their budget limits.
 *
 * Trigger: Every hour (configurable in application.properties)
 * Firestore path checked: /users/{userId}/budgets/{budgetId}
 */
@Component
public class BudgetAlertScheduler {

    private final BudgetAlertService budgetAlertService;

    public BudgetAlertScheduler(BudgetAlertService budgetAlertService) {
        this.budgetAlertService = budgetAlertService;
    }

    /**
     * Hourly budget check.
     * Reads all user budgets from Firestore and triggers alerts where needed.
     *
     * Cron: Every hour at minute 0  (configurable via scheduler.budget-check.cron)
     */
    @Scheduled(cron = "${scheduler.budget-check.cron:0 0 * * * ?}")
    public void runHourlyBudgetCheck() {
        System.out.println("[BudgetAlertScheduler] Running hourly budget check...");

        try {
            // Fetch all users from Firestore
            QuerySnapshot usersSnapshot = FirestoreClient.getFirestore()
                    .collection("users")
                    .get()
                    .get();

            List<QueryDocumentSnapshot> users = usersSnapshot.getDocuments();
            System.out.println("[BudgetAlertScheduler] Checking " + users.size() + " users...");

            for (QueryDocumentSnapshot userDoc : users) {
                String userId = userDoc.getId();
                checkUserBudgets(userId);
            }

        } catch (Exception e) {
            System.err.println("[BudgetAlertScheduler] Error during budget check: " + e.getMessage());
        }
    }

    /**
     * Check all budget categories for a specific user.
     * Firestore path: /users/{userId}/budgets/{budgetId}
     */
    private void checkUserBudgets(String userId) {
        try {
            QuerySnapshot budgetsSnapshot = FirestoreClient.getFirestore()
                    .collection("users")
                    .document(userId)
                    .collection("budgets")
                    .get()
                    .get();

            for (QueryDocumentSnapshot budgetDoc : budgetsSnapshot.getDocuments()) {
                Double budgetLimit = budgetDoc.getDouble("budgetLimit");
                Double amountSpent = budgetDoc.getDouble("amountSpent");
                String categoryName = budgetDoc.getString("categoryName");

                if (budgetLimit == null || amountSpent == null || categoryName == null) continue;
                if (budgetLimit <= 0) continue;

                BudgetAlertDTO dto = new BudgetAlertDTO();
                dto.setUserId(userId);
                dto.setCategoryName(categoryName);
                dto.setBudgetLimit(budgetLimit);
                dto.setAmountSpent(amountSpent);

                budgetAlertService.checkAndAlert(dto);
            }

        } catch (Exception e) {
            System.err.println("[BudgetAlertScheduler] Error checking user " + userId + ": " + e.getMessage());
        }
    }
}
