import 'package:flutter/foundation.dart';
import 'package:frontend/data/secure_storage.dart';

class SecureStorageProvider extends ChangeNotifier {
  bool _isLoading = false;
  String? _error;

  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<void> write(String key, String value) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      await SecureStorage.write(key, value);
    } catch (e) {
      _error = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<String?> read(String key) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final result = await SecureStorage.read(key);
      return result;
    } catch (e) {
      _error = e.toString();
      return null;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> deleteAll() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      await SecureStorage.deleteAll();
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