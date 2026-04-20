class RentalImage {
  final String id;
  final String extension;

  RentalImage({
    required this.id,
    required this.extension,
  });

  factory RentalImage.fromJson(Map<String, dynamic> json) {
    return RentalImage(
      id: json['id'] as String,
      extension: json['extension'] as String,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'extension': extension,
    };
  }
}
