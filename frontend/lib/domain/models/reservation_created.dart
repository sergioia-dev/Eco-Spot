class ReservationCreated {
  final String id;
  final double totalPrice;

  ReservationCreated({
    required this.id,
    required this.totalPrice,
  });

  factory ReservationCreated.fromJson(Map<String, dynamic> json) {
    return ReservationCreated(
      id: (json['id'] as String?) ?? '',
      totalPrice: (json['totalPrice'] as num?)?.toDouble() ?? 0.0,
    );
  }
}