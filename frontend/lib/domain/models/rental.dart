import 'package:frontend/domain/models/rental_image.dart';

class Rental {
  final String id;
  final String name;
  final String? description;
  final String contact;
  final int size;
  final int peopleQuantity;
  final int rooms;
  final int bathrooms;
  final String city;
  final String country;
  final String? location;
  final double valueNight;
  final bool isEnable;
  final double reviewAverage;
  final List<RentalImage> images;

  Rental({
    required this.id,
    required this.name,
    this.description,
    required this.contact,
    required this.size,
    required this.peopleQuantity,
    required this.rooms,
    required this.bathrooms,
    required this.city,
    required this.country,
    this.location,
    required this.valueNight,
    required this.isEnable,
    required this.reviewAverage,
    required this.images,
  });

  factory Rental.fromJson(Map<String, dynamic> json) {
    return Rental(
      id: json['id'] as String,
      name: json['name'] as String,
      description: json['description'] as String?,
      contact: json['contact'] as String,
      size: json['size'] as int,
      peopleQuantity: json['peopleQuantity'] as int,
      rooms: json['rooms'] as int,
      bathrooms: json['bathrooms'] as int,
      city: json['city'] as String,
      country: json['country'] as String,
      location: json['location'] as String?,
      valueNight: (json['valueNight'] as num).toDouble(),
      isEnable: json['isEnable'] as bool,
      reviewAverage: (json['reviewAverage'] as num).toDouble(),
      images: (json['images'] as List<dynamic>?)
              ?.map((e) => RentalImage.fromJson(e as Map<String, dynamic>))
              .toList() ??
          [],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'description': description,
      'contact': contact,
      'size': size,
      'peopleQuantity': peopleQuantity,
      'rooms': rooms,
      'bathrooms': bathrooms,
      'city': city,
      'country': country,
      'location': location,
      'valueNight': valueNight,
      'isEnable': isEnable,
      'reviewAverage': reviewAverage,
      'images': images.map((e) => e.toJson()).toList(),
    };
  }
}
