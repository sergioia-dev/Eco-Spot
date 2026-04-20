import 'package:flutter/material.dart';
import 'package:frontend/data/repository_implementations/auth_repository.dart';
import 'package:frontend/presentation/views/host/host_bundle.dart';
import 'package:frontend/presentation/views/auth/auth_bundle.dart';

class Routes {
  static const String initialRoute = signInScreen;

  static const String signInScreen = 'signin';
  static const String signUpScreen = 'signup';
  static const String homeScreen = 'home';
  static const String touristHomeScreen = 'tourist_home';
  static const String hostHomeScreen = 'host_home';
  static const String businessHomeScreen = 'business_home';
  static const String adminHomeScreen = 'admin_home';
  static const String createRentalScreen = 'create_rental';

  static Map<String, Widget Function(BuildContext)> routes = {
    signInScreen: (context) => SignInScreen(authInterface: AuthRepository()),
    signUpScreen: (context) => SignUpScreen(authInterface: AuthRepository()),
    homeScreen: (context) =>
        const Scaffold(body: Center(child: Text('Home Screen'))),
    touristHomeScreen: (context) =>
        const Scaffold(body: Center(child: Text('Tourist Home Screen'))),
    hostHomeScreen: (context) => const HostHomeScreen(),
    createRentalScreen: (context) => const Scaffold(
      body: Center(child: Text('Create Rental Screen - Coming Soon')),
    ),
    businessHomeScreen: (context) =>
        const Scaffold(body: Center(child: Text('Business Home Screen'))),
    adminHomeScreen: (context) =>
        const Scaffold(body: Center(child: Text('Admin Home Screen'))),
  };
}
