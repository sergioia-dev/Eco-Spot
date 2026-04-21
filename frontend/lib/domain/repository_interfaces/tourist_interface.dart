import 'package:frontend/domain/models/reservation_created.dart';
import 'package:frontend/domain/models/payment.dart';
import 'package:frontend/domain/models/review.dart';
import 'package:frontend/domain/models/tourist_item.dart';

abstract class TouristInterface {
  Future<ReservationCreated?> createReservation({
    required String token,
    required String rentalId,
    required String startingDate,
    required String endDate,
  });

  Future<Payment?> createPayment({
    required String token,
    required String reservationId,
    required double amount,
  });

  Future<Review?> createReview({
    required String token,
    required String rentalId,
    required int qualification,
    String? opinion,
  });

  Future<bool> cancelReservation({
    required String token,
    required String reservationId,
  });

  Future<TouristItem?> getItemsByLocation({
    required String token,
  });

  Future<SearchResult?> searchItems({
    required String token,
    required String searchBy,
    String? category,
  });

  Future<List<Map<String, dynamic>>> getUserReservations({
    required String token,
    bool upcoming = true,
  });
}