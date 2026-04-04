import 'dart:convert';
import 'package:frontend/data/secure_storage.dart';
import 'package:frontend/domain/repository_interfaces/auth_interface.dart';
import 'package:http/http.dart' as http;

class AuthRepository implements AuthInterface {
  final String baseUrl = 'http://10.0.2.2:8080/api/auth';
  final http.Client _client = http.Client();

  @override
  Future<bool> signIn(String email, String password) async {
    final response = await _client.post(
      Uri.parse('$baseUrl/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'email': email, 'password': password}),
    );

    if (response.statusCode == 200) {
      final token = response.body;
      SecureStorage.write("token", token);
      return true;
    } else {
      return false;
    }
  }
}
