package com.smartbudget.service;

import com.smartbudget.dto.BudgetAlertDTO;
import com.smartbudget.dto.NotificationDTO;
import com.smartbudget.model.AppNotification;
import com.smartbudget.model.UserSettings;
import com.smartbudget.repository.UserSettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BudgetAlertService
 * Member 4 – Budget Alert Logic Tests
 */
class BudgetAlertServiceTest {

    private BudgetAlertService budgetAlertService;
    private NotificationService notificationService;
    private UserSettingsRepository settingsRepository;

    @BeforeEach
    void setUp() {
        notificationService = Mockito.mock(NotificationService.class);
        settingsRepository  = Mockito.mock(UserSettingsRepository.class);
        budgetAlertService  = new BudgetAlertService(notificationService, settingsRepository);
    }

    @Test
    void shouldSendWarningWhenBudgetReaches80Percent() throws Exception {
        // Arrange
        UserSettings settings = UserSettings.builder()
                .notificationsEnabled(true)
                .budgetWarningAlerts(true)
                .budgetExceededAlerts(true)
                .build();
        when(settingsRepository.findByUserId("user1")).thenReturn(Optional.of(settings));

        BudgetAlertDTO dto = new BudgetAlertDTO();
        dto.setUserId("user1");
        dto.setCategoryName("Food");
        dto.setBudgetLimit(100.0);
        dto.setAmountSpent(85.0);  // 85% → should trigger WARNING

        // Act
        budgetAlertService.checkAndAlert(dto);

        // Assert: sendNotification called once for WARNING
        verify(notificationService, times(1)).sendNotification(any(NotificationDTO.class));
    }

    @Test
    void shouldSendExceededWhenBudgetOver100Percent() throws Exception {
        // Arrange
        UserSettings settings = UserSettings.builder()
                .notificationsEnabled(true)
                .budgetWarningAlerts(true)
                .budgetExceededAlerts(true)
                .build();
        when(settingsRepository.findByUserId("user2")).thenReturn(Optional.of(settings));

        BudgetAlertDTO dto = new BudgetAlertDTO();
        dto.setUserId("user2");
        dto.setCategoryName("Transport");
        dto.setBudgetLimit(100.0);
        dto.setAmountSpent(110.0);  // 110% → should trigger EXCEEDED

        // Act
        budgetAlertService.checkAndAlert(dto);

        // Assert: sendNotification called once for EXCEEDED
        verify(notificationService, times(1)).sendNotification(any(NotificationDTO.class));
    }

    @Test
    void shouldNotSendAlertWhenBudgetBelow80Percent() throws Exception {
        // Arrange
        UserSettings settings = UserSettings.builder()
                .notificationsEnabled(true)
                .budgetWarningAlerts(true)
                .build();
        when(settingsRepository.findByUserId("user3")).thenReturn(Optional.of(settings));

        BudgetAlertDTO dto = new BudgetAlertDTO();
        dto.setUserId("user3");
        dto.setCategoryName("Entertainment");
        dto.setBudgetLimit(100.0);
        dto.setAmountSpent(50.0);  // 50% → no alert

        // Act
        budgetAlertService.checkAndAlert(dto);

        // Assert: no notification sent
        verify(notificationService, never()).sendNotification(any());
    }

    @Test
    void shouldNotSendAlertWhenUserDisabledBudgetWarnings() throws Exception {
        // Arrange: user turned off budget warning alerts
        UserSettings settings = UserSettings.builder()
                .notificationsEnabled(true)
                .budgetWarningAlerts(false)   // disabled
                .budgetExceededAlerts(false)  // disabled
                .build();
        when(settingsRepository.findByUserId("user4")).thenReturn(Optional.of(settings));

        BudgetAlertDTO dto = new BudgetAlertDTO();
        dto.setUserId("user4");
        dto.setCategoryName("Food");
        dto.setBudgetLimit(100.0);
        dto.setAmountSpent(95.0);  // would normally trigger

        // Act
        budgetAlertService.checkAndAlert(dto);

        // Assert: no notification because user opted out
        verify(notificationService, never()).sendNotification(any());
    }
}
