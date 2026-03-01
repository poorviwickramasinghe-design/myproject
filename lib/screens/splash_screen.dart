import 'package:flutter/material.dart';
import 'dart:async';
import 'onboarding_screen.dart'; // Ensure this path is correct

class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> {
  Timer? _timer;

  @override
  void initState() {
    super.initState();
    _startNavigationTimer();
  }

  void _startNavigationTimer() {
    // Show splash for 3 seconds
    _timer = Timer(const Duration(seconds: 3), () {
      if (mounted) {
        _navigateToOnboarding();
      }
    });
  }

  void _navigateToOnboarding() {
    Navigator.pushReplacement(
      context,
      PageRouteBuilder(
        pageBuilder: (context, animation, secondaryAnimation) => const OnboardingScreen(),
        transitionsBuilder: (context, animation, secondaryAnimation, child) {
          // Smooth fade transition into Onboarding
          return FadeTransition(opacity: animation, child: child);
        },
        transitionDuration: const Duration(milliseconds: 800),
      ),
    );
  }

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Image.asset(
              'assets/images/logo.png',
              height: 100,
              errorBuilder: (context, error, stackTrace) {
                return const Icon(
                  Icons.account_balance_wallet,
                  size: 65,
                  color: Color(0xFF2B90B6),
                );
              },
            ),
            const SizedBox(height: 24),
            const Text(
              "SMART STUDENT",
              style: TextStyle(
                fontSize: 28,
                fontWeight: FontWeight.bold,
                color: Color(0xFF2B90B6),
                letterSpacing: 1.2,
              ),
            ),
            const Text(
              "BUDGET TRACKER",
              style: TextStyle(
                fontSize: 16,
                color: Colors.grey,
                letterSpacing: 4,
              ),
            ),
            const SizedBox(height: 50),
            // Loading indicator in Lime Green
            const CircularProgressIndicator(
              strokeWidth: 3,
              valueColor: AlwaysStoppedAnimation<Color>(Color(0xFFADCF35)),
            ),
          ],
        ),
      ),
    );
  }
}