import 'package:flutter/material.dart';
import 'package:frontend/presentation/routes/routes.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Feel Chat',
      debugShowCheckedModeBanner: false,
      initialRoute: Routes.initialRoute,
      routes: Routes.routes,
    );
  }
}
