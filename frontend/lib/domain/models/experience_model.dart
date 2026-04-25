class ExperienceModel {
  String? id;
  String name;
  String? description;
  String contact;
  double price;
  String city;
  String country;
  String? location;
  DateTime startingDate;
  DateTime endDate;
  bool isEnable;
  List<String> images;

  ExperienceModel({
    this.id,
    required this.name,
    this.description,
    required this.contact,
    required this.price,
    required this.city,
    required this.country,
    this.location,
    required this.startingDate,
    required this.endDate,
    this.isEnable = true,
    this.images = const [],
  });

  factory ExperienceModel.fromJson(Map<String, dynamic> json) {
    return ExperienceModel(
      id: json['id']?.toString(),
      name: json['name'] ?? '',
      description: json['description'],
      contact: json['contact'] ?? '',
      price: (json['price'] ?? 0).toDouble(),
      city: json['city'] ?? '',
      country: json['country'] ?? '',
      location: json['location'],
      // Parseo de fechas desde String ISO8601
      startingDate: json['startingDate'] != null 
          ? DateTime.parse(json['startingDate']) 
          : DateTime.now(),
      endDate: json['endDate'] != null 
          ? DateTime.parse(json['endDate']) 
          : DateTime.now(),
      isEnable: json['isEnable'] ?? true,
      images: List<String>.from(json['images'] ?? []),
    );
  }

  Map<String, dynamic> toJson() => {
        'id': id,
        'name': name,
        'description': description,
        'contact': contact,
        'price': price,
        'city': city,
        'country': country,
        'location': location,
        'startingDate': startingDate.toIso8601String(),
        'endDate': endDate.toIso8601String(),
        'isEnable': isEnable,
        'images': images,
      };
}