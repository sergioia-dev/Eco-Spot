import 'dart:io';
import 'package:frontend/domain/models/rental.dart';
import 'package:frontend/domain/models/reservation.dart';

abstract class HostInterface {
  Future<Rental?> createRental({
    required String token,
    required String name,
    String? description,
    required String contact,
    required int size,
    required int peopleQuantity,
    required int rooms,
    required int bathrooms,
    required String city,
    required String country,
    String? location,
    required double valueNight,
    List<File>? images,
  });

  Future<Rental?> updateRental({
    required String token,
    required String rentalId,
    required String name,
    String? description,
    required String contact,
    required int size,
    required int peopleQuantity,
    required int rooms,
    required int bathrooms,
    required String city,
    required String country,
    String? location,
    required double valueNight,
    List<File>? images,
  });

  Future<bool> deleteRental({required String token, required String rentalId});

  Future<List<Rental>> getRentals({
    required String token,
    bool includeDisabled = false,
  });

  Future<Rental?> toggleRentalEnable({
    required String token,
    required String rentalId,
    required bool enabled,
  });

  Future<List<Reservation>> getReservations({
    required String token,
    required String rentalId,
    bool upcoming = true,
  });

  Future<bool> cancelReservation({
    required String token,
    required String reservationId,
  });
}
