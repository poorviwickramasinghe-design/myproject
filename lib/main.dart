import 'package:flutter/material.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:provider/provider.dart';
import 'firebase_options.dart';

// Import your screens
import 'providers/theme_provider_screen.dart';
import 'screens/splash_screen.dart';
import 'screens/onboarding_screen.dart';
import 'screens/login_screen.dart';
import 'screens/dashboard_screen.dart';
import 'screens/signup_screen.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  try {
    await Firebase.initializeApp(
      options: DefaultFirebaseOptions.currentPlatform,
    ).timeout(const Duration(seconds: 15));
    debugPrint("✅ Firebase initialized successfully");
  } catch (e) {
    debugPrint("❌ Firebase Initialization Error: $e");
  }

  runApp(
    ChangeNotifierProvider(
      create: (context) => ThemeProvider(),
      child: const SmartBudgetApp(),
    ),
  );
}

class SmartBudgetApp extends StatelessWidget {
  const SmartBudgetApp({super.key});

  @override
  Widget build(BuildContext context) {
    const Color kTealColor = Color(0xFF2B90B6);
    const Color kLimeColor = Color(0xFFADCF35);
    final themeProvider = Provider.of<ThemeProvider>(context);

    return MaterialApp(
      title: 'Smart Student Budget Tracker',
      debugShowCheckedModeBanner: false,
      themeMode: themeProvider.themeMode,

      theme: ThemeData(
        useMaterial3: true,
        brightness: Brightness.light,
        colorScheme: ColorScheme.fromSeed(seedColor: kTealColor, primary: kTealColor, secondary: kLimeColor),
        scaffoldBackgroundColor: Colors.white,
        inputDecorationTheme: _buildInputTheme(kTealColor, Brightness.light),
      ),

      darkTheme: ThemeData(
        useMaterial3: true,
        brightness: Brightness.dark,
        colorScheme: ColorScheme.fromSeed(seedColor: kTealColor, primary: kTealColor, secondary: kLimeColor, brightness: Brightness.dark),
        scaffoldBackgroundColor: const Color(0xFF121212),
        inputDecorationTheme: _buildInputTheme(kTealColor, Brightness.dark),
      ),

      // Define named routes
      routes: {
        '/onboarding': (context) => const OnboardingScreen(),
        '/login': (context) => const LoginScreen(),
        '/signup': (context) => const SignupScreen(),
        '/dashboard': (context) => const DashboardScreen(),
      },

      // Start with the RootWrapper
      home: const RootWrapper(),
    );
  }

  InputDecorationTheme _buildInputTheme(Color primaryColor, Brightness brightness) {
    bool isDark = brightness == Brightness.dark;
    return InputDecorationTheme(
      filled: true,
      fillColor: isDark ? Colors.grey[900] : Colors.grey[50],
      border: OutlineInputBorder(borderRadius: BorderRadius.circular(12), borderSide: BorderSide.none),
      enabledBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: BorderSide(color: isDark ? Colors.grey[800]! : Colors.grey.shade300),
      ),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: BorderSide(color: primaryColor, width: 2),
      ),
    );
  }
}

/// MODIFIED: Always starts with SplashScreen
class RootWrapper extends StatelessWidget {
  const RootWrapper({super.key});

  @override
  Widget build(BuildContext context) {
    // We always return SplashScreen first.
    // The SplashScreen.dart logic will handle the timer and push to Onboarding.
    return const SplashScreen();
  }
}