import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:frontend/presentation/routes/routes.dart';
import 'package:frontend/domain/providers/secure_storage_provider.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (_) => SecureStorageProvider(),
      child: MaterialApp(
        title: 'Feel Chat',
        debugShowCheckedModeBanner: false,
        initialRoute: Routes.initialRoute,
        routes: Routes.routes,
      ),
    );
  }
}
