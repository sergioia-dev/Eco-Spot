class Review {
  final String id;
  final int qualification;
  final String? opinion;
  final DateTime creationDate;

  Review({
    required this.id,
    required this.qualification,
    this.opinion,
    required this.creationDate,
  });

  factory Review.fromJson(Map<String, dynamic> json) {
    return Review(
      id: (json['id'] as String?) ?? '',
      qualification: (json['qualification'] as int?) ?? 0,
      opinion: json['opinion'] as String?,
      creationDate: json['creationDate'] != null
          ? DateTime.parse(json['creationDate'] as String)
          : DateTime.now(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'qualification': qualification,
      'opinion': opinion,
      'creationDate': creationDate.toIso8601String(),
    };
  }
}