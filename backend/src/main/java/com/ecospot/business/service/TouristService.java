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

import com.ecospot.business.dato.CreateReservationRequest;
import com.ecospot.business.dato.CreateReservationResponse;
import com.ecospot.business.dato.CreateReviewRequest;
import com.ecospot.business.dato.ItemCategory;
import com.ecospot.business.dato.ItemsResponse;
import com.ecospot.business.dato.PaymentStatus;
import com.ecospot.business.dato.ReservationResponse;
import com.ecospot.business.dato.RentalResponse;
import com.ecospot.business.dato.RentalResponse.ImageInfo;
import com.ecospot.persistance.entity.Business;
import com.ecospot.persistance.entity.Experience;
import com.ecospot.persistance.entity.Payment;
import com.ecospot.persistance.entity.Rental;
import com.ecospot.persistance.entity.Reservation;
import com.ecospot.persistance.entity.Review;
import com.ecospot.persistance.entity.User;
import com.ecospot.persistance.repository.BusinessRepository;
import com.ecospot.persistance.repository.ExperienceRepository;
import com.ecospot.persistance.repository.ImageRepository;
import com.ecospot.persistance.repository.PaymentRepository;
import com.ecospot.persistance.repository.RentalRepository;
import com.ecospot.persistance.repository.ReservationRepository;
import com.ecospot.persistance.repository.ReviewRepository;
import com.ecospot.persistance.repository.UserRepository;
import com.ecospot.util.JWT;

@Service
public class TouristService {
  private static final Logger logger = LoggerFactory.getLogger(TouristService.class);

  private final JWT jwt;
  private final RentalRepository rentalRepository;
  private final BusinessRepository businessRepository;
  private final ExperienceRepository experienceRepository;
  private final ImageRepository imageRepository;
  private final UserRepository userRepository;
  private final ReservationRepository reservationRepository;
  private final ReviewRepository reviewRepository;
  private final PaymentRepository paymentRepository;

  public TouristService(JWT jwt, RentalRepository rentalRepository, BusinessRepository businessRepository,
      ExperienceRepository experienceRepository, ImageRepository imageRepository, UserRepository userRepository,
      ReservationRepository reservationRepository, ReviewRepository reviewRepository,
      PaymentRepository paymentRepository) {
    this.jwt = jwt;
    this.rentalRepository = rentalRepository;
    this.businessRepository = businessRepository;
    this.experienceRepository = experienceRepository;
    this.imageRepository = imageRepository;
    this.userRepository = userRepository;
    this.reservationRepository = reservationRepository;
    this.reviewRepository = reviewRepository;
    this.paymentRepository = paymentRepository;
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

  public CreateReservationResponse createReservation(String token, UUID rentalId, CreateReservationRequest request) {
    if (!isValidTouristToken(token)) {
      logger.warn("Invalid token for createReservation");
      return null;
    }

    UUID userId = jwt.getUserId(token);
    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      logger.warn("User not found: {}", userId);
      return null;
    }

    Optional<Rental> rentalOpt = rentalRepository.findById(rentalId);
    if (rentalOpt.isEmpty()) {
      logger.warn("Rental not found: {}", rentalId);
      return null;
    }

    if (!rentalOpt.get().isEnable()) {
      logger.warn("Rental {} is not enabled", rentalId);
      return null;
    }

    LocalDate startDate = request.getStartingDate();
    LocalDate endDate = request.getEndDate();

    if (startDate == null || endDate == null) {
      logger.warn("Missing dates for reservation");
      return null;
    }

    if (startDate.isAfter(endDate) || startDate.isBefore(LocalDate.now())) {
      logger.warn("Invalid dates for reservation");
      return null;
    }

    List<Reservation> existingReservations = reservationRepository.findByRentalIdAndIsCancelledFalse(rentalId);
    for (Reservation res : existingReservations) {
      if (datesOverlap(startDate, endDate, res.getStartingDate(), res.getEndDate())) {
        logger.warn("Dates overlap with existing reservation: {}", res.getId());
        return null;
      }
    }

    try {
      Reservation reservation = new Reservation(userOpt.get(), rentalOpt.get(), startDate, endDate);
      Reservation savedReservation = reservationRepository.save(reservation);
      
      long nights = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
      double totalPrice = rentalOpt.get().getValueNight() * nights;
      
      Payment payment = new Payment(userOpt.get(), PaymentStatus.SUCCESS, totalPrice);
      payment.setReservation(savedReservation);
      paymentRepository.save(payment);
      
      logger.info("Reservation created successfully: {}", savedReservation.getId());
      return new CreateReservationResponse(savedReservation.getId(), totalPrice);

    } catch (Exception e) {
      logger.error("Error creating reservation: {}", e.getMessage(), e);
      return null;
    }
  }

  private boolean datesOverlap(LocalDate newStart, LocalDate newEnd,
      LocalDate existingStart, LocalDate existingEnd) {
    return !newEnd.isBefore(existingStart) && !newStart.isAfter(existingEnd);
  }

  public boolean createReview(String token, UUID rentalId, CreateReviewRequest request) {
    if (!isValidTouristToken(token)) {
      logger.warn("Invalid token for createReview");
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

    Integer qualification = request.getQualification();
    if (qualification == null || qualification < 1 || qualification > 5) {
      logger.warn("Invalid qualification: {}", qualification);
      return false;
    }

    boolean hasCompletedReservation = reservationRepository.findByRentalIdAndUserIdAndEndDateBefore(
        rentalId, userId, LocalDate.now()).size() > 0;

    if (!hasCompletedReservation) {
      logger.warn("User {} has no completed reservation for rental {}", userId, rentalId);
      return false;
    }

    boolean alreadyReviewed = reviewRepository.existsByRentalIdAndUserId(rentalId, userId);
    if (alreadyReviewed) {
      logger.warn("User {} already reviewed rental {}", userId, rentalId);
      return false;
    }

    try {
      Review review = new Review(userOpt.get(), rentalOpt.get(), qualification, request.getOpinion());
      reviewRepository.save(review);
      logger.info("Review created successfully for rental: {}", rentalId);
      return true;

    } catch (Exception e) {
      logger.error("Error creating review: {}", e.getMessage(), e);
      return false;
    }
  }

  public List<ReservationResponse> getUserReservations(String token, boolean upcoming) {
    if (!isValidTouristToken(token)) {
      logger.warn("Invalid token for getUserReservations");
      return null;
    }

    UUID userId = jwt.getUserId(token);
    LocalDate today = LocalDate.now();

    List<Reservation> reservations;
    if (upcoming) {
      reservations = reservationRepository.findByUserIdAndEndDateAfter(userId, today);
    } else {
      reservations = reservationRepository.findByUserIdAndEndDateBefore(userId, today);
    }

    return reservations.stream()
        .filter(r -> !r.isCancelled())
        .map(this::toReservationResponse)
        .toList();
  }

  private ReservationResponse toReservationResponse(Reservation reservation) {
    Rental rental = reservation.getRental();
    List<ImageInfo> images = imageRepository.findByRentalId(rental.getId()).stream()
        .map(img -> new ImageInfo(img.getId(), img.getExtension()))
        .toList();

    double reviewAverage = calculateReviewAverage(rental.getId());

    long nights = reservation.getEndDate().toEpochDay() - reservation.getStartingDate().toEpochDay();
    double price = nights * rental.getValueNight();

    ReservationResponse response = new ReservationResponse();
    response.setReservationId(reservation.getId());
    response.setId(rental.getId());
    response.setName(rental.getName());
    response.setDescription(rental.getDescription());
    response.setContact(rental.getContact());
    response.setSize(rental.getSize());
    response.setPeopleQuantity(rental.getPeopleQuantity());
    response.setRooms(rental.getRooms());
    response.setBathrooms(rental.getBathrooms());
    response.setCity(rental.getCity());
    response.setCountry(rental.getCountry());
    response.setLocation(rental.getLocation());
    response.setValueNight(rental.getValueNight());
    response.setEnable(rental.isEnable());
    response.setReviewAverage(reviewAverage);
    response.setImages(images);

    response.setStartingDate(reservation.getStartingDate());
    response.setEndDate(reservation.getEndDate());
    response.setPrice(price);
    response.setCancelled(reservation.isCancelled());

    return response;
  }

  private RentalResponse toRentalResponse(Rental rental) {
    List<ImageInfo> images = imageRepository.findByRentalId(rental.getId()).stream()
        .map(img -> new ImageInfo(img.getId(), img.getExtension()))
        .toList();

    double reviewAverage = calculateReviewAverage(rental.getId());

    return new RentalResponse(
        rental.getId(),
        rental.getName(),
        rental.getDescription(),
        rental.getContact(),
        rental.getSize(),
        rental.getPeopleQuantity(),
        rental.getRooms(),
        rental.getBathrooms(),
        rental.getCity(),
        rental.getCountry(),
        rental.getLocation(),
        rental.getValueNight(),
        rental.isEnable(),
        reviewAverage,
        images);
  }

  private double calculateReviewAverage(UUID rentalId) {
    List<Review> reviews = reviewRepository.findByRentalId(rentalId);
    if (reviews.isEmpty()) {
      return 0.0;
    }

    double sum = reviews.stream()
        .mapToInt(Review::getQualification)
        .sum();

    double average = sum / reviews.size();
    return Math.round(average * 10.0) / 10.0;
  }

  public boolean createPayment(String token, UUID reservationId, Double amount) {
    if (!isValidTouristToken(token)) {
      logger.warn("Invalid token for createPayment");
      return false;
    }

    UUID userId = jwt.getUserId(token);
    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      logger.warn("User not found: {}", userId);
      return false;
    }

    Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
    if (reservationOpt.isEmpty()) {
      logger.warn("Reservation not found: {}", reservationId);
      return false;
    }

    Reservation reservation = reservationOpt.get();

    if (!reservation.getUser().getId().equals(userId)) {
      logger.warn("User {} is not authorized to pay for reservation {}", userId, reservationId);
      return false;
    }

    Rental rental = reservation.getRental();
    long nights = java.time.temporal.ChronoUnit.DAYS.between(reservation.getStartingDate(), reservation.getEndDate());
    double totalPrice = rental.getValueNight() * nights;

    if (amount == null || Math.abs(amount - totalPrice) > 0.01) {
      logger.warn("Invalid amount: {}. Expected: {}", amount, totalPrice);
      return false;
    }

    try {
      Payment payment = new Payment(userOpt.get(), PaymentStatus.SUCCESS, amount);
      payment.setReservation(reservation);
      paymentRepository.save(payment);

      logger.info("Payment created successfully for reservation: {}", reservationId);
      return true;

    } catch (Exception e) {
      logger.error("Error creating payment: {}", e.getMessage(), e);
      return false;
    }
  }

}
