import 'dart:io';
import 'package:frontend/domain/models/experience_model.dart';

abstract class ExperienceInterface {
  Future<ExperienceModel?> createExperience({ // Cambiado de Interface a Model
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
  });

  Future<ExperienceModel?> updateExperience({ // Cambiado de Interface a Model
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
  });

  Future<bool> deleteExperience({
    required String token, 
    required String experienceId
  });

  Future<List<ExperienceModel>> getExperiences({ // Cambiado de Interface a Model
    required String token,
    bool includeDisabled = false,
  });

  Future<bool> toggleExperienceEnable({
    required String token,
    required String experienceId,
    required bool enabled,
  });
}