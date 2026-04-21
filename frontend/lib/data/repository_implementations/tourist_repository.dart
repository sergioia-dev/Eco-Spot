import 'dart:convert';
import 'package:frontend/domain/models/reservation_created.dart';
import 'package:frontend/domain/models/payment.dart';
import 'package:frontend/domain/models/review.dart';
import 'package:frontend/domain/models/tourist_item.dart';
import 'package:frontend/domain/repository_interfaces/tourist_interface.dart';
import 'package:http/http.dart' as http;

class TouristRepository implements TouristInterface {
  final String baseUrl = 'http://10.0.2.2:8080/api/v1/tourist';
  final http.Client _client = http.Client();

  Map<String, String> _headers(String token) => {
    'Authorization': 'Bearer $token',
    'Content-Type': 'application/json',
  };

  @override
  Future<ReservationCreated?> createReservation({
    required String token,
    required String rentalId,
    required String startingDate,
    required String endDate,
  }) async {
    final uri = Uri.parse('$baseUrl/rentals/$rentalId/reservations');

    final response = await _client.post(
      uri,
      headers: _headers(token),
      body: jsonEncode({'startingDate': startingDate, 'endDate': endDate}),
    );

    if (response.statusCode == 201 && response.body.isNotEmpty) {
      return ReservationCreated.fromJson(
        jsonDecode(response.body) as Map<String, dynamic>,
      );
    }
    return null;
  }

  @override
  Future<Payment?> createPayment({
    required String token,
    required String reservationId,
    required double amount,
  }) async {
    final uri = Uri.parse('$baseUrl/payments');

    final response = await _client.post(
      uri,
      headers: _headers(token),
      body: jsonEncode({'reservationId': reservationId, 'amount': amount}),
    );

    if (response.statusCode == 201 && response.body.isNotEmpty) {
      return Payment.fromJson(
        jsonDecode(response.body) as Map<String, dynamic>,
      );
    }
    return null;
  }

  @override
  Future<Review?> createReview({
    required String token,
    required String rentalId,
    required int qualification,
    String? opinion,
  }) async {
    final uri = Uri.parse('$baseUrl/rentals/$rentalId/reviews');

    final response = await _client.post(
      uri,
      headers: _headers(token),
      body: jsonEncode({
        'qualification': qualification,
        'opinion': opinion ?? '',
      }),
    );

    if (response.statusCode == 201 && response.body.isNotEmpty) {
      return Review.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
    }
    return null;
  }

  @override
  Future<bool> cancelReservation({
    required String token,
    required String reservationId,
  }) async {
    final uri = Uri.parse('$baseUrl/reservations/$reservationId/cancel');

    final response = await _client.patch(uri, headers: _headers(token));

    return response.statusCode == 200;
  }

  @override
  Future<TouristItem?> getItemsByLocation({required String token}) async {
    final uri = Uri.parse('$baseUrl/items');

    final response = await _client.get(uri, headers: _headers(token));

    if (response.statusCode == 200 && response.body.isNotEmpty) {
      return TouristItem.fromJson(
        jsonDecode(response.body) as Map<String, dynamic>,
      );
    }
    return null;
  }

  @override
  Future<SearchResult?> searchItems({
    required String token,
    required String searchBy,
    String? category,
  }) async {
    final uri = Uri.parse('$baseUrl/search').replace(
      queryParameters: {
        'searchBy': searchBy,
        if (category != null) 'category': category,
      },
    );

    final response = await _client.get(uri, headers: _headers(token));

    if (response.statusCode == 200 && response.body.isNotEmpty) {
      return SearchResult.fromJson(
        jsonDecode(response.body) as Map<String, dynamic>,
      );
    }
    return null;
  }

  @override
  Future<List<Map<String, dynamic>>> getUserReservations({
    required String token,
    bool upcoming = true,
  }) async {
    final uri = Uri.parse(
      '$baseUrl/reservations',
    ).replace(queryParameters: {'upcoming': upcoming.toString()});

    final response = await _client.get(uri, headers: _headers(token));

    if (response.statusCode == 200 && response.body.isNotEmpty) {
      final List<dynamic> jsonList = jsonDecode(response.body) as List<dynamic>;
      return jsonList.map((e) => e as Map<String, dynamic>).toList();
    }
    return [];
  }
}

