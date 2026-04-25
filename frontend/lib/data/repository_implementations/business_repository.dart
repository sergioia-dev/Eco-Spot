import 'dart:convert';
import 'dart:io';

import 'package:frontend/domain/models/business_model.dart';
import 'package:frontend/domain/repository_interfaces/business_interface.dart';
import 'package:http/http.dart' as http;

class BusinessRepository implements BusinessInterface {
  final String baseUrl = 'http://10.0.2.2:8080/api/v1/auth';

  @override
  Future<BusinessModel?> createBusiness({ // Agregado el ? para que coincida
    required String token,
    required String name,
    String? description,
    required String contact,
    required String city,
    required String country,
    String? location,
    String? menu,
    required bool isEnable,
    List<File>? images,
  }) async {
    try {
      var request = http.MultipartRequest('POST', Uri.parse(baseUrl));
      request.headers['Authorization'] = 'Bearer $token';

      request.fields['name'] = name;
      request.fields['description'] = description ?? "";
      request.fields['contact'] = contact;
      request.fields['city'] = city;
      request.fields['country'] = country;
      request.fields['location'] = location ?? "";
      request.fields['menu'] = menu ?? "";
      request.fields['isEnable'] = isEnable.toString();

      if (images != null) {
        for (var file in images) {
          request.files.add(await http.MultipartFile.fromPath('images', file.path));
        }
      }

      var streamedResponse = await request.send();
      var response = await http.Response.fromStream(streamedResponse);

      if (response.statusCode == 201) {
        return BusinessModel.fromJson(json.decode(response.body));
      }
      return null; // Ahora sí permite devolver null
    } catch (e) {
      print("Error en createBusiness: $e");
      return null;
    }
  }

  // Asegúrate de que updateBusiness también tenga el ? en el repo
 @override
  Future<BusinessModel?> updateBusiness({
    required String token,
    required String businessId,
    required String name,
    String? description,
    required String contact,
    required String city,
    required String country,
    String? location,
    String? menu,
    required bool isEnable,
    List<File>? images,
  }) async {
    try {
      // 1. Configurar la petición (usando PUT o PATCH según tu API)
      var request = http.MultipartRequest('PUT', Uri.parse('$baseUrl/$businessId'));
      
      request.headers['Authorization'] = 'Bearer $token';

      // 2. Mapear los campos
      request.fields['name'] = name;
      request.fields['description'] = description ?? "";
      request.fields['contact'] = contact;
      request.fields['city'] = city;
      request.fields['country'] = country;
      request.fields['location'] = location ?? "";
      request.fields['menu'] = menu ?? "";
      request.fields['isEnable'] = isEnable.toString();

      // 3. Adjuntar imágenes si hay nuevas
      if (images != null && images.isNotEmpty) {
        for (var file in images) {
          request.files.add(await http.MultipartFile.fromPath('images', file.path));
        }
      }

      // 4. Enviar y procesar
      var streamedResponse = await request.send();
      var response = await http.Response.fromStream(streamedResponse);

      if (response.statusCode == 200) {
        return BusinessModel.fromJson(json.decode(response.body));
      }

      // Si el status no es 200, retornamos null
      return null; 

    } catch (e) {
      print("Error en updateBusiness: $e");
      return null; // Importante: retornar null aquí también
    }
    
    // El error de "ends without returning a value" se soluciona 
    // asegurando que cada camino (if, else, catch) tenga su return.
  }}