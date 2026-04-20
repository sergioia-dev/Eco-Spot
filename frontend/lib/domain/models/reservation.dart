class Reservation {
  final String id;
  final String rentalId;
  final String rentalName;
  final String userName;
  final String userSurname;
  final String startingDate;
  final String endDate;
  final bool isCancelled;

  Reservation({
    required this.id,
    required this.rentalId,
    required this.rentalName,
    required this.userName,
    required this.userSurname,
    required this.startingDate,
    required this.endDate,
    required this.isCancelled,
  });

  factory Reservation.fromJson(Map<String, dynamic> json) {
    return Reservation(
      id: json['id'] as String,
      rentalId: json['rentalId'] as String,
      rentalName: json['rentalName'] as String,
      userName: json['userName'] as String,
      userSurname: json['userSurname'] as String,
      startingDate: json['startingDate'] as String,
      endDate: json['endDate'] as String,
      isCancelled: json['isCancelled'] as bool,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'rentalId': rentalId,
      'rentalName': rentalName,
      'userName': userName,
      'userSurname': userSurname,
      'startingDate': startingDate,
      'endDate': endDate,
      'isCancelled': isCancelled,
    };
  }
}
