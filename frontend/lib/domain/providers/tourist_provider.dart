import 'package:flutter/foundation.dart';
import 'package:frontend/data/repository_implementations/tourist_repository.dart';
import 'package:frontend/data/repository_implementations/host_repository.dart';
import 'package:frontend/domain/models/reservation_created.dart';
import 'package:frontend/domain/models/payment.dart';
import 'package:frontend/domain/models/review.dart';
import 'package:frontend/domain/models/tourist_item.dart';

class TouristProvider extends ChangeNotifier {
  final TouristRepository _touristRepository = TouristRepository();
  final HostRepository _hostRepository = HostRepository();

  TouristItem? _touristItem;
  SearchResult? _searchResults;
  List<Map<String, dynamic>> _userReservations = [];
  bool _isLoading = false;
  String? _error;

  TouristItem? get touristItem => _touristItem;
  SearchResult? get searchResults => _searchResults;
  List<Map<String, dynamic>> get userReservations => _userReservations;
  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<ReservationCreated?> createReservation({
    required String token,
    required String rentalId,
    required String startingDate,
    required String endDate,
  }) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final result = await _touristRepository.createReservation(
        token: token,
        rentalId: rentalId,
        startingDate: startingDate,
        endDate: endDate,
      );
      _isLoading = false;
      notifyListeners();
      return result;
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      notifyListeners();
      return null;
    }
  }

  Future<Payment?> createPayment({
    required String token,
    required String reservationId,
    required double amount,
  }) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final result = await _touristRepository.createPayment(
        token: token,
        reservationId: reservationId,
        amount: amount,
      );
      _isLoading = false;
      notifyListeners();
      return result;
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      notifyListeners();
      return null;
    }
  }

  Future<Review?> createReview({
    required String token,
    required String rentalId,
    required int qualification,
    String? opinion,
  }) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final result = await _touristRepository.createReview(
        token: token,
        rentalId: rentalId,
        qualification: qualification,
        opinion: opinion,
      );
      _isLoading = false;
      notifyListeners();
      return result;
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      notifyListeners();
      return null;
    }
  }

  Future<bool> cancelReservation({
    required String token,
    required String reservationId,
  }) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final result = await _touristRepository.cancelReservation(
        token: token,
        reservationId: reservationId,
      );
      _isLoading = false;
      notifyListeners();
      return result;
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }

  Future<bool> loadItemsByLocation(String token) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _touristItem = await _touristRepository.getItemsByLocation(token: token);
      _isLoading = false;
      notifyListeners();
      return _touristItem != null;
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }

  Future<bool> searchItems({
    required String token,
    required String searchBy,
    String? category,
  }) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _searchResults = await _touristRepository.searchItems(
        token: token,
        searchBy: searchBy,
        category: category,
      );
      _isLoading = false;
      notifyListeners();
      return _searchResults != null;
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }

  Future<bool> loadUserReservations(String token, {bool upcoming = true}) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _userReservations = await _touristRepository.getUserReservations(
        token: token,
        upcoming: upcoming,
      );
      _isLoading = false;
      notifyListeners();
      return true;
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }

  Future<bool> cancelReservationHost({
    required String token,
    required String reservationId,
  }) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final result = await _hostRepository.cancelReservation(
        token: token,
        reservationId: reservationId,
      );
      _isLoading = false;
      notifyListeners();
      return result;
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }

  void clearError() {
    _error = null;
    notifyListeners();
  }
}