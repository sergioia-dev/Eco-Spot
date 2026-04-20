import 'dart:convert';
import 'package:frontend/domain/repository_interfaces/auth_interface.dart';
import 'package:http/http.dart' as http;

class AuthRepository implements AuthInterface {
  final String baseUrl = 'http://10.0.2.2:8080/api/v1/auth';
  final http.Client _client = http.Client();

  @override
  Future<String?> signIn(String email, String password) async {
    final response = await _client.post(
      Uri.parse('$baseUrl/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'email': email, 'password': password}),
    );

    if (response.statusCode == 200) {
      return response.body;
    } else {
      return null;
    }
  }

  @override
  Future<bool> signUp({
    required String name,
    required String surname,
    required String city,
    required String country,
    required String email,
    required String password,
    required String rol,
  }) async {
    final response = await _client.post(
      Uri.parse('$baseUrl/register'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'name': name,
        'surname': surname,
        'city': city,
        'country': country,
        'email': email,
        'password': password,
        'rol': rol,
      }),
    );

    return response.statusCode == 201;
  }
}
