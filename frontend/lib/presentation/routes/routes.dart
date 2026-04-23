import 'package:flutter/material.dart';
import 'package:frontend/data/repository_implementations/auth_repository.dart';
import 'package:frontend/domain/models/rental.dart';
import 'package:frontend/presentation/views/host/host_bundle.dart';
import 'package:frontend/presentation/views/tourist/tourist_bundle.dart';
import 'package:frontend/presentation/views/host/reservations_screen.dart';
import 'package:frontend/presentation/views/auth/auth_bundle.dart';
import 'package:frontend/presentation/views/splash_screen.dart';

class Routes {
  static const String initialRoute = splashScreen;

  static const String splashScreen = 'splash';
  static const String signInScreen = 'signin';
  static const String signUpScreen = 'signup';
  static const String homeScreen = 'home';
  static const String touristHomeScreen = 'tourist_home';
  static const String hostHomeScreen = 'host_home';
  static const String businessHomeScreen = 'business_home';
  static const String adminHomeScreen = 'admin_home';
  static const String createRentalScreen = 'create_rental';
  static const String reservationsScreen = 'reservations';
  static const String touristSearchScreen = 'tourist_search';
  static const String touristReservationsScreen = 'tourist_reservations';
  static const String touristProfileScreen = 'tourist_profile';
  static const String touristRentalDetailScreen = 'tourist_rental_detail';
  static const String touristReservationFormScreen = 'tourist_reservation_form';
  static const String touristPaymentScreen = 'tourist_payment';
  static const String touristReviewScreen = 'tourist_review';

  static Map<String, Widget Function(BuildContext)> routes = {
    splashScreen: (context) => const SplashScreen(),
    signInScreen: (context) => SignInScreen(authInterface: AuthRepository()),
    signUpScreen: (context) => SignUpScreen(authInterface: AuthRepository()),
    homeScreen: (context) =>
        const Scaffold(body: Center(child: Text('Home Screen'))),
    touristHomeScreen: (context) => const TouristHomeScreen(),
    touristSearchScreen: (context) => const TouristSearchScreen(),
    touristReservationsScreen: (context) => const TouristReservationsScreen(),
    touristProfileScreen: (context) => const TouristProfileScreen(),
    touristRentalDetailScreen: (context) {
      final rental =
          ModalRoute.of(context)!.settings.arguments as Rental;
      return TouristRentalDetailScreen(rental: rental);
    },
    touristReservationFormScreen: (context) {
      final args =
          ModalRoute.of(context)!.settings.arguments as Map<String, dynamic>;
      return TouristCreateReservationScreen(
        rentalId: args['rentalId'] as String,
        rentalName: args['rentalName'] as String,
        valueNight: args['valueNight'] as double,
      );
    },
    touristPaymentScreen: (context) {
      final args =
          ModalRoute.of(context)!.settings.arguments as Map<String, dynamic>;
      return TouristPaymentScreen(
        rentalId: args['rentalId'] as String,
        rentalName: args['rentalName'] as String,
        startDate: args['startDate'] as String,
        endDate: args['endDate'] as String,
        totalPrice: args['totalPrice'] as double,
        nights: args['nights'] as int,
      );
    },
    touristReviewScreen: (context) {
      final args =
          ModalRoute.of(context)!.settings.arguments as Map<String, dynamic>;
      return TouristCreateReviewScreen(
        rentalId: args['rentalId'] as String,
        rentalName: args['rentalName'] as String,
      );
    },
    hostHomeScreen: (context) => const HostHomeScreen(),
    createRentalScreen: (context) => const RentalFormScreen(),
    reservationsScreen: (context) {
      final args =
          ModalRoute.of(context)!.settings.arguments as Map<String, String>;
      return ReservationsScreen(
        rentalId: args['rentalId']!,
        rentalName: args['rentalName']!,
      );
    },
    businessHomeScreen: (context) =>
        const Scaffold(body: Center(child: Text('Business Home Screen'))),
    adminHomeScreen: (context) =>
        const Scaffold(body: Center(child: Text('Admin Home Screen'))),
  };
}
