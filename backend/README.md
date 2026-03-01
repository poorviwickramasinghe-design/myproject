# Smart Student Budget Tracker – Java Backend

Spring Boot REST API backend for the Smart Student Budget Tracker Flutter app.

---

## Coverage

| Member | Responsibility | Screens |
|--------|---------------|---------|
| **Member 4** | Notifications, Budget Alerts, Monthly Summary | `notifications_screen.dart`, `dashboard_screen.dart`, `settings_screen.dart` |
| **Member 5** | Security, Profile, Settings, Help & Support | `profile_screen.dart`, `settings_screen.dart`, `help_support_screen.dart`, `select_currency_screen.dart` |

---

## Project Structure

```
backend/
├── pom.xml                          ← Maven build file (Java 17, Spring Boot 3.2)
├── src/
│   ├── main/
│   │   ├── java/com/smartbudget/
│   │   │   ├── SmartBudgetApplication.java      ← Entry point
│   │   │   ├── config/
│   │   │   │   ├── FirebaseConfig.java           ← Firebase Admin SDK init
│   │   │   │   └── SecurityConfig.java           ← Spring Security + CORS
│   │   │   ├── controller/
│   │   │   │   ├── HealthController.java         ← GET /api/health
│   │   │   │   ├── NotificationController.java   ← Member 4: /api/notifications
│   │   │   │   ├── ProfileController.java        ← Member 5: /api/profile
│   │   │   │   ├── SettingsController.java       ← Member 4+5: /api/settings
│   │   │   │   └── HelpSupportController.java    ← Member 5: /api/help
│   │   │   ├── service/
│   │   │   │   ├── NotificationService.java      ← FCM + Firestore notification logic
│   │   │   │   ├── BudgetAlertService.java       ← 80%/100% threshold logic
│   │   │   │   ├── ProfileService.java           ← Profile CRUD
│   │   │   │   ├── SettingsService.java          ← Settings + FCM token management
│   │   │   │   └── HelpSupportService.java       ← Help message submission
│   │   │   ├── model/
│   │   │   │   ├── AppNotification.java          ← Notification entity
│   │   │   │   ├── UserProfile.java              ← User profile entity
│   │   │   │   ├── UserSettings.java             ← Settings + notification prefs entity
│   │   │   │   └── HelpSupportMessage.java       ← Help message entity
│   │   │   ├── repository/
│   │   │   │   ├── NotificationRepository.java   ← Firestore /users/{uid}/notifications
│   │   │   │   ├── UserProfileRepository.java    ← Firestore /users/{uid}
│   │   │   │   └── UserSettingsRepository.java   ← Firestore /users/{uid}/settings
│   │   │   ├── dto/
│   │   │   │   ├── NotificationDTO.java
│   │   │   │   ├── BudgetAlertDTO.java
│   │   │   │   ├── ProfileUpdateDTO.java
│   │   │   │   ├── CurrencyPreferenceDTO.java
│   │   │   │   ├── SettingsUpdateDTO.java
│   │   │   │   └── HelpMessageDTO.java
│   │   │   ├── scheduler/
│   │   │   │   ├── BudgetAlertScheduler.java     ← Hourly budget check
│   │   │   │   └── MonthlySummaryScheduler.java  ← 1st of month summary
│   │   │   └── util/
│   │   │       ├── FirebaseTokenFilter.java      ← JWT/Firebase token validation
│   │   │       └── ValidationUtil.java           ← Custom data validation
│   │   └── resources/
│   │       ├── application.properties            ← Config (port, Firebase, thresholds)
│   │       ├── firestore.rules                   ← Firestore security rules
│   │       └── firebase-service-account.json     ← ⚠ Replace with real key (never commit)
│   └── test/
│       └── java/com/smartbudget/
│           ├── SmartBudgetApplicationTest.java
│           └── service/BudgetAlertServiceTest.java
```

---

## REST API Reference

### Health Check (public)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/health` | Check if backend is running |

### Notifications (Member 4)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/notifications` | Get all notifications for user |
| POST | `/api/notifications/send` | Send push notification |
| POST | `/api/notifications/check-budget` | Trigger budget alert check |
| PUT | `/api/notifications/{id}/read` | Mark notification as read |
| PUT | `/api/notifications/read-all` | Mark all as read |
| DELETE | `/api/notifications/{id}` | Delete a notification |

### Profile (Member 5)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/profile` | Get own profile |
| PUT | `/api/profile` | Update name/username/image |
| PUT | `/api/profile/currency` | Update currency preference |
| POST | `/api/profile/init` | Initialize profile after signup |

### Settings (Member 4 + 5)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/settings` | Get all settings + notification prefs |
| PUT | `/api/settings` | Update all settings |
| POST | `/api/settings/fcm-token` | Register device FCM token |

### Help & Support (Member 5)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/help` | Submit a help/support message |

---

## Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- Firebase project with Firestore + FCM enabled

### 1. Add Firebase Service Account
1. Go to **Firebase Console** → Project Settings → Service Accounts
2. Click **Generate new private key**
3. Replace `src/main/resources/firebase-service-account.json` with the downloaded file
4. **Never commit the real key to Git**

### 2. Update application.properties
```properties
firebase.database-url=https://YOUR-PROJECT-ID-default-rtdb.firebaseio.com
```

### 3. Deploy Firestore Security Rules
```bash
firebase deploy --only firestore:rules
```
(Uses `src/main/resources/firestore.rules`)

### 4. Run the backend
```bash
mvn spring-boot:run
```

### 5. Test
```bash
# Health check (no auth needed)
curl http://localhost:8080/api/health
```

---

## Authentication Flow

Every API call from Flutter must include a Firebase ID token:
```
Authorization: Bearer <firebase-id-token>
```

The `FirebaseTokenFilter` verifies the token and extracts the `uid`.
All operations are automatically scoped to that `uid` — enforcing:
```
allow read, write: if request.auth.uid == userId
```

---

## Notification Triggers

| Trigger | Threshold | Type |
|---------|-----------|------|
| Budget Warning | ≥ 80% spent | `BUDGET_WARNING` |
| Budget Exceeded | ≥ 100% spent | `BUDGET_EXCEEDED` |
| Monthly Summary | 1st of month, 08:00 | `MONTHLY_SUMMARY` |
| Scheduled Budget Check | Every hour | Automatic |

---

## Firestore Data Structure

```
Firestore
├── users/
│   └── {userId}/
│       ├── (profile fields: fullName, username, email, currency...)
│       ├── settings/
│       │   └── preferences   ← UserSettings document
│       ├── notifications/
│       │   └── {notificationId}   ← AppNotification documents
│       ├── budgets/
│       │   └── {budgetId}    ← Budget documents (from other members)
│       └── transactions/
│           └── {transactionId}   ← Transaction documents
└── helpMessages/
    └── {messageId}           ← HelpSupportMessage documents
```
