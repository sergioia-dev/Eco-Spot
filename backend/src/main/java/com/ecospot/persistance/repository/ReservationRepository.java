package com.ecospot.persistance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecospot.persistance.entity.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

  List<Reservation> findByRentalIdAndStartingDateAfter(UUID rentalId, LocalDate dateTime);

  List<Reservation> findByRentalIdAndStartingDateAfterAndIsCancelledFalse(UUID rentalId, LocalDate dateTime);

  List<Reservation> findByRentalIdAndStartingDateBefore(UUID rentalId, LocalDate dateTime);

  List<Reservation> findByRentalIdAndStartingDateBeforeAndIsCancelledFalse(UUID rentalId, LocalDate dateTime);

  List<Reservation> findByRentalIdAndIsCancelledFalse(UUID rentalId);

}