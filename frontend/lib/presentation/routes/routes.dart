import 'package:flutter/material.dart';
import 'package:frontend/data/repository_implementations/auth_repository.dart';
import 'package:frontend/presentation/views/sign_in_screen.dart';

class Routes {
  static const String initialRoute = signInScreen;

  static const String signInScreen = 'signin';
  static const String signUpScreen = 'signup';
  static const String homeScreen = 'home';

  static Map<String, Widget Function(BuildContext)> routes = {
    signInScreen: (context) => SignInScreen(authInterface: AuthRepository()),
    signUpScreen: (context) => const Scaffold(
      body: Center(child: Text('Sign Up Screen - Coming Soon')),
    ),
    homeScreen: (context) => const Scaffold(
      body: Center(child: Text('Home Screen')),
    ),
  };
}
