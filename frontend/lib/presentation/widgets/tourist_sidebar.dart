import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:frontend/domain/providers/secure_storage_provider.dart';

class TouristSidebar extends StatelessWidget {
  const TouristSidebar({super.key});

  @override
  Widget build(BuildContext context) {
    return Drawer(
      child: ListView(
        padding: EdgeInsets.zero,
        children: [
          const DrawerHeader(
            decoration: BoxDecoration(color: Color(0xFFFF385C)),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(Icons.account_circle, size: 48, color: Colors.white),
                SizedBox(height: 8),
                Text(
                  'Eco Spot',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 22,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
          ),
          ListTile(
            leading: const Icon(Icons.settings),
            title: const Text('Settings'),
            onTap: () {
              // TODO: Navigate to settings
            },
          ),
          ListTile(
            leading: const Icon(Icons.help),
            title: const Text('Help'),
            onTap: () {
              // TODO: Navigate to help
            },
          ),
          const Divider(),
          ListTile(
            leading: const Icon(Icons.logout, color: Colors.red),
            title: const Text('Logout', style: TextStyle(color: Colors.red)),
            onTap: () async {
              final secureStorage = context.read<SecureStorageProvider>();
              await secureStorage.deleteAll();
              if (context.mounted) {
                Navigator.pushReplacementNamed(context, 'signin');
              }
            },
          ),
        ],
      ),
    );
  }
}

