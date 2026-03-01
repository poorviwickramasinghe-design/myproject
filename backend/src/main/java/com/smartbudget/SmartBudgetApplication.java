package com.smartbudget;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Smart Student Budget Tracker - Main Application Entry Point
 *
 * Backend covers:
 *   Member 4 – Notifications (FCM push alerts, budget triggers, monthly summary)
 *   Member 5 – Security, Profile & App Settings (Firestore rules, profile, currency, help)
 */
@SpringBootApplication
@EnableScheduling
public class SmartBudgetApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartBudgetApplication.class, args);
    }
}
