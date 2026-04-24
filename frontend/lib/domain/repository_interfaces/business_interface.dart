import 'dart:io';

import 'package:frontend/domain/models/business_model.dart';

abstract class BusinessInterface {
  Future<BusinessModel?> createBusiness({ // Cambiado de bool? a BusinessModel?
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
  });

  Future<BusinessModel?> updateBusiness({ // Cambiado de bool? a BusinessModel?
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
  });
  
  // ... el resto se queda igual
}