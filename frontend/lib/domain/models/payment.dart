class Payment {
  final String id;
  final String reservationId;
  final double amount;
  final String status;

  Payment({
    required this.id,
    required this.reservationId,
    required this.amount,
    required this.status,
  });

  factory Payment.fromJson(Map<String, dynamic> json) {
    return Payment(
      id: (json['id'] as String?) ?? '',
      reservationId: (json['reservationId'] as String?) ?? '',
      amount: (json['amount'] as num?)?.toDouble() ?? 0.0,
      status: (json['status'] as String?) ?? '',
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'reservationId': reservationId,
      'amount': amount,
      'status': status,
    };
  }
}