import 'dart:convert';
import 'dart:io';

import 'package:frontend/domain/models/experience_model.dart';
import 'package:frontend/domain/repository_interfaces/experience_interface.dart';
import 'package:http/http.dart' as http;

class ExperienceRepository implements ExperienceInterface {
  final String baseUrl = 'http://10.0.2.2:8080/api/v1/auth';

  @override
  Future<ExperienceModel?> createExperience({
    required String token,
    required String name,
    String? description,
    required String contact,
    required double price,
    required String city,
    required String country,
    String? location,
    required DateTime startingDate,
    required DateTime endDate,
    required bool isEnable,
    List<File>? images,
  }) async {
    try {
      var request = http.MultipartRequest('POST', Uri.parse(baseUrl));
      request.headers['Authorization'] = 'Bearer $token';

      request.fields['name'] = name;
      request.fields['description'] = description ?? "";
      request.fields['contact'] = contact;
      request.fields['price'] = price.toString();
      request.fields['city'] = city;
      request.fields['country'] = country;
      request.fields['location'] = location ?? "";
      request.fields['startingDate'] = startingDate.toIso8601String();
      request.fields['endDate'] = endDate.toIso8601String();
      request.fields['isEnable'] = isEnable.toString();

      if (images != null) {
        for (var file in images) {
          request.files.add(await http.MultipartFile.fromPath('images', file.path));
        }
      }

      var streamedResponse = await request.send();
      var response = await http.Response.fromStream(streamedResponse);

      if (response.statusCode == 201) {
        return ExperienceModel.fromJson(json.decode(response.body));
      }
      return null;
    } catch (e) {
      print("Error en createExperience: $e");
      return null;
    }
  }

  @override
  Future<ExperienceModel?> updateExperience({
    required String token,
    required String experienceId,
    required String name,
    String? description,
    required String contact,
    required double price,
    required String city,
    required String country,
    String? location,
    required DateTime startingDate,
    required DateTime endDate,
    required bool isEnable,
    List<File>? images,
  }) async {
    try {
      var request = http.MultipartRequest('PUT', Uri.parse('$baseUrl/$experienceId'));
      request.headers['Authorization'] = 'Bearer $token';

      request.fields['name'] = name;
      request.fields['description'] = description ?? "";
      request.fields['contact'] = contact;
      request.fields['price'] = price.toString();
      request.fields['city'] = city;
      request.fields['country'] = country;
      request.fields['location'] = location ?? "";
      request.fields['startingDate'] = startingDate.toIso8601String();
      request.fields['endDate'] = endDate.toIso8601String();
      request.fields['isEnable'] = isEnable.toString();

      if (images != null && images.isNotEmpty) {
        for (var file in images) {
          request.files.add(await http.MultipartFile.fromPath('images', file.path));
        }
      }

      var streamedResponse = await request.send();
      var response = await http.Response.fromStream(streamedResponse);

      if (response.statusCode == 200) {
        return ExperienceModel.fromJson(json.decode(response.body));
      }
      return null;
    } catch (e) {
      print("Error en updateExperience: $e");
      return null;
    }
  }

  @override
  Future<bool> deleteExperience({required String token, required String experienceId}) async {
    try {
      final response = await http.delete(
        Uri.parse('$baseUrl/$experienceId'),
        headers: {'Authorization': 'Bearer $token'},
      );
      return response.statusCode == 200;
    } catch (e) {
      return false;
    }
  }

  @override
  Future<List<ExperienceModel>> getExperiences({required String token, bool includeDisabled = false}) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl?includeDisabled=$includeDisabled'),
        headers: {'Authorization': 'Bearer $token'},
      );

      if (response.statusCode == 200) {
        List<dynamic> body = json.decode(response.body);
        return body.map((item) => ExperienceModel.fromJson(item)).toList();
      }
      return [];
    } catch (e) {
      return [];
    }
  }

  @override
  Future<bool> toggleExperienceEnable({
    required String token,
    required String experienceId,
    required bool enabled,
  }) async {
    try {
      final response = await http.patch(
        Uri.parse('$baseUrl/$experienceId/toggle'),
        headers: {
          'Authorization': 'Bearer $token',
          'Content-Type': 'application/json',
        },
        body: json.encode({'isEnable': enabled}),
      );
      return response.statusCode == 200;
    } catch (e) {
      return false;
    }
  }
}