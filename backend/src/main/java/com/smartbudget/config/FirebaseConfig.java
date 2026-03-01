package com.smartbudget.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

/**
 * Firebase Configuration
 *
 * Initializes Firebase Admin SDK used for:
 *   - Firestore (user profiles, notifications, settings)
 *   - Firebase Cloud Messaging / FCM (push notifications)
 *   - Firebase Auth token verification
 */
@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account-path}")
    private Resource serviceAccountResource;

    @Value("${firebase.database-url}")
    private String databaseUrl;

    /**
     * Initializes Firebase Admin SDK on startup.
     * Requires firebase-service-account.json in src/main/resources/.
     */
    @PostConstruct
    public void initializeFirebase() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream serviceAccount = serviceAccountResource.getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(databaseUrl)
                    .build();

            FirebaseApp.initializeApp(options);
            System.out.println("[FirebaseConfig] Firebase Admin SDK initialized successfully.");
        }
    }
}
