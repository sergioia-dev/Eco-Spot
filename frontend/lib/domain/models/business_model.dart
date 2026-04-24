class BusinessModel {
  String? id;
  String name;
  String? description;
  String contact;
  String city;
  String country;
  String? location;
  String? menu;
  bool isEnable;
  List<String> images;

  BusinessModel({
    this.id,
    required this.name,
    this.description,
    required this.contact,
    required this.city,
    required this.country,
    this.location,
    this.menu,
    this.isEnable = true,
    this.images = const [],
  });

  // Convierte un JSON de la API a un objeto de Dart
  factory BusinessModel.fromJson(Map<String, dynamic> json) {
    return BusinessModel(
      id: json['id']?.toString(),
      name: json['name'] ?? '',
      description: json['description'],
      contact: json['contact'] ?? '',
      city: json['city'] ?? '',
      country: json['country'] ?? '',
      location: json['location'],
      menu: json['menu'],
      isEnable: json['isEnable'] ?? true,
      images: List<String>.from(json['images'] ?? []),
    );
  }

  // Convierte el objeto a JSON para enviarlo a la API (si no usas Multipart)
  Map<String, dynamic> toJson() => {
        'id': id,
        'name': name,
        'description': description,
        'contact': contact,
        'city': city,
        'country': country,
        'location': location,
        'menu': menu,
        'isEnable': isEnable,
        'images': images,
      };
}