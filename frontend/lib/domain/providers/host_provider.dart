import 'package:flutter/foundation.dart';
import 'package:frontend/data/repository_implementations/host_repository.dart';
import 'package:frontend/domain/models/rental.dart';

class HostProvider extends ChangeNotifier {
  final HostRepository _hostRepository = HostRepository();

  List<Rental> _rentals = [];
  bool _isLoading = false;
  String? _error;

  List<Rental> get rentals => _rentals;
  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<void> loadRentals(String token, {bool includeDisabled = false}) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _rentals = await _hostRepository.getRentals(
        token: token,
        includeDisabled: includeDisabled,
      );
    } catch (e) {
      _error = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  void clearError() {
    _error = null;
    notifyListeners();
  }
}