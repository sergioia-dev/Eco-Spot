package com.ecospot.business.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ecospot.business.dato.CreateReservationRequest;
import com.ecospot.business.dato.ItemCategory;
import com.ecospot.business.dato.ItemsResponse;
import com.ecospot.persistance.entity.Business;
import com.ecospot.persistance.entity.Experience;
import com.ecospot.persistance.entity.Rental;
import com.ecospot.persistance.entity.Reservation;
import com.ecospot.persistance.entity.User;
import com.ecospot.persistance.repository.BusinessRepository;
import com.ecospot.persistance.repository.ExperienceRepository;
import com.ecospot.persistance.repository.RentalRepository;
import com.ecospot.persistance.repository.ReservationRepository;
import com.ecospot.persistance.repository.UserRepository;
import com.ecospot.util.JWT;

@Service
public class TouristService {
  private static final Logger logger = LoggerFactory.getLogger(TouristService.class);

  private final JWT jwt;
  private final RentalRepository rentalRepository;
  private final BusinessRepository businessRepository;
  private final ExperienceRepository experienceRepository;
  private final UserRepository userRepository;
  private final ReservationRepository reservationRepository;

  public TouristService(JWT jwt, RentalRepository rentalRepository, BusinessRepository businessRepository,
      ExperienceRepository experienceRepository, UserRepository userRepository,
      ReservationRepository reservationRepository) {
    this.jwt = jwt;
    this.rentalRepository = rentalRepository;
    this.businessRepository = businessRepository;
    this.experienceRepository = experienceRepository;
    this.userRepository = userRepository;
    this.reservationRepository = reservationRepository;
  }

  private boolean isValidTouristToken(String token) {
    if (!jwt.validateToken(token)) {
      return false;
    }
    return "TOURIST".equals(jwt.getRol(token));
  }

  public ItemsResponse getItemsByLocation(String token) {
    if (!isValidTouristToken(token)) {
      logger.warn("Invalid token provided for getItemsByLocation");
      return null;
    }

    Optional<User> userOpt = getUserFromToken(token);
    if (userOpt.isEmpty()) {
      logger.warn("User not found: {}", jwt.getUserId(token));
      return null;
    }

    User user = userOpt.get();
    String city = user.getCurrentCity();
    String country = user.getCurrentCountry();

    if (city != null && country != null) {
      city = city.toUpperCase();
      country = country.toUpperCase();
    }

    List<Rental> rentals = rentalRepository.findByCityAndCountry(city, country);
    List<Business> businesses = businessRepository.findByCityAndCountry(city, country);
    List<Experience> experiences = experienceRepository.findByCityAndCountry(city, country);

    int limit = 10;
    rentals = rentals.size() > limit ? rentals.subList(0, limit) : rentals;
    businesses = businesses.size() > limit ? businesses.subList(0, limit) : businesses;
    experiences = experiences.size() > limit ? experiences.subList(0, limit) : experiences;

    return new ItemsResponse(experiences, rentals, businesses);
  }

  public List<Object> search(String token, ItemCategory category, String searchBy) {
    if (!isValidTouristToken(token)) {
      logger.warn("Invalid token provided for search");
      return null;
    }

    String userCountry = getUserCountry(token);
    List<Object> results = searchByCategory(category, searchBy);

    if (results == null) {
      return null;
    }

    return sortByUserCountry(results, userCountry);
  }

  private Optional<User> getUserFromToken(String token) {
    UUID userId = jwt.getUserId(token);
    return userRepository.findById(userId);
  }

  private String getUserCountry(String token) {
    return getUserFromToken(token)
        .filter(u -> u.getCurrentCountry() != null)
        .map(u -> u.getCurrentCountry().toUpperCase())
        .orElse(null);
  }

  private List<Object> searchByCategory(ItemCategory category, String searchBy) {
    if (category != null) {
      return switch (category) {
        case RENTAL -> new ArrayList<>(rentalRepository.findByNameContainingIgnoreCase(searchBy));
        case BUSINESS -> new ArrayList<>(businessRepository.findByNameContainingIgnoreCase(searchBy));
        case EXPERIENCE -> new ArrayList<>(experienceRepository.findByNameContainingIgnoreCase(searchBy));
      };
    }

    List<Object> allResults = new ArrayList<>();
    allResults.addAll(businessRepository.findByNameContainingIgnoreCase(searchBy));
    allResults.addAll(experienceRepository.findByNameContainingIgnoreCase(searchBy));
    allResults.addAll(rentalRepository.findByNameContainingIgnoreCase(searchBy));
    return allResults;
  }

  private List<Object> sortByUserCountry(List<Object> results, String userCountry) {
    if (userCountry == null) {
      return results;
    }

    final String finalUserCountry = userCountry;
    return results.stream()
        .sorted((a, b) -> {
          boolean aIsUserCountry = finalUserCountry.equalsIgnoreCase(getCountry(a));
          boolean bIsUserCountry = finalUserCountry.equalsIgnoreCase(getCountry(b));
          if (aIsUserCountry && !bIsUserCountry)
            return -1;
          if (!aIsUserCountry && bIsUserCountry)
            return 1;
          return 0;
        })
        .collect(Collectors.toList());
  }

  private String getCountry(Object obj) {
    if (obj instanceof Business b)
      return b.getCountry();
    if (obj instanceof Experience e)
      return e.getCountry();
    if (obj instanceof Rental r)
      return r.getCountry();
    return null;
  }

  public boolean createReservation(String token, UUID rentalId, CreateReservationRequest request) {
    if (!isValidTouristToken(token)) {
      logger.warn("Invalid token for createReservation");
      return false;
    }

    UUID userId = jwt.getUserId(token);
    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      logger.warn("User not found: {}", userId);
      return false;
    }

    Optional<Rental> rentalOpt = rentalRepository.findById(rentalId);
    if (rentalOpt.isEmpty()) {
      logger.warn("Rental not found: {}", rentalId);
      return false;
    }

    if (!rentalOpt.get().isEnable()) {
      logger.warn("Rental {} is not enabled", rentalId);
      return false;
    }

    LocalDate startDate = request.getStartingDate();
    LocalDate endDate = request.getEndDate();

    if (startDate == null || endDate == null) {
      logger.warn("Missing dates for reservation");
      return false;
    }

    if (startDate.isAfter(endDate) || startDate.isBefore(LocalDate.now())) {
      logger.warn("Invalid dates for reservation");
      return false;
    }

    List<Reservation> existingReservations = reservationRepository.findByRentalIdAndIsCancelledFalse(rentalId);
    for (Reservation res : existingReservations) {
      if (datesOverlap(startDate, endDate, res.getStartingDate(), res.getEndDate())) {
        logger.warn("Dates overlap with existing reservation: {}", res.getId());
        return false;
      }
    }

    try {
      Reservation reservation = new Reservation(userOpt.get(), rentalOpt.get(), startDate, endDate);
      reservationRepository.save(reservation);
      logger.info("Reservation created successfully: {}", reservation.getId());
      return true;

    } catch (Exception e) {
      logger.error("Error creating reservation: {}", e.getMessage(), e);
      return false;
    }
  }

  private boolean datesOverlap(LocalDate newStart, LocalDate newEnd, 
      LocalDate existingStart, LocalDate existingEnd) {
    return !newEnd.isBefore(existingStart) && !newStart.isAfter(existingEnd);
  }

}
