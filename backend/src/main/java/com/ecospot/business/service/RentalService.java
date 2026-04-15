package com.ecospot.business.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ecospot.business.dato.CreateRentalRequest;
import com.ecospot.business.dato.RentalResponse;
import com.ecospot.business.dato.RentalResponse.ImageInfo;
import com.ecospot.business.dato.ReservationResponse;
import com.ecospot.business.dato.UpdateRentalRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ecospot.persistance.entity.Image;
import com.ecospot.persistance.entity.Rental;
import com.ecospot.persistance.entity.Reservation;
import com.ecospot.persistance.entity.Review;
import com.ecospot.persistance.entity.User;
import com.ecospot.persistance.repository.ImageRepository;
import com.ecospot.persistance.repository.RentalRepository;
import com.ecospot.persistance.repository.ReservationRepository;
import com.ecospot.persistance.repository.ReviewRepository;
import com.ecospot.persistance.repository.ReservationRepository;
import com.ecospot.persistance.repository.UserRepository;
import com.ecospot.util.ImageStorage;
import com.ecospot.util.ImageStorage.SavedImage;
import com.ecospot.util.JWT;

@Service
public class RentalService {
  private static final Logger logger = LoggerFactory.getLogger(RentalService.class);

  private final JWT jwt;
  private final UserRepository userRepository;
  private final RentalRepository rentalRepository;
  private final ImageRepository imageRepository;
  private final ImageStorage imageStorage;
  private final ReservationRepository reservationRepository;
  private final ReviewRepository reviewRepository;

  public RentalService(JWT jwt, UserRepository userRepository, RentalRepository rentalRepository,
      ImageRepository imageRepository, ImageStorage imageStorage, 
      ReservationRepository reservationRepository, ReviewRepository reviewRepository) {
    this.jwt = jwt;
    this.userRepository = userRepository;
    this.rentalRepository = rentalRepository;
    this.imageRepository = imageRepository;
    this.imageStorage = imageStorage;
    this.reservationRepository = reservationRepository;
    this.reviewRepository = reviewRepository;
  }

  private boolean isValidHostToken(String token) {
    if (!jwt.validateToken(token)) {
      logger.warn("Invalid token provided for rental operation");
      return false;
    }
    return "HOST".equals(jwt.getRol(token));
  }

  private boolean isValidHostOrAdminToken(String token) {
    if (!jwt.validateToken(token)) {
      logger.warn("Invalid token provided for rental operation");
      return false;
    }
    String role = jwt.getRol(token);
    return "HOST".equals(role) || "ADMIN".equals(role);
  }

  public boolean updateRental(String token, UUID rentalId, UpdateRentalRequest request) {
    if (!isValidHostOrAdminToken(token)) {
      logger.warn("Invalid token for updateRental");
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

    Rental rental = rentalOpt.get();
    String userRole = jwt.getRol(token);

    boolean isOwner = rental.getUser().getId().equals(userId);
    boolean isAdmin = "ADMIN".equals(userRole);

    if (!isOwner && !isAdmin) {
      logger.warn("User {} is not authorized to update rental {}", userId, rentalId);
      return false;
    }

    if (request.getName() == null || request.getName().isEmpty() ||
        request.getContact() == null || request.getContact().isEmpty() ||
        request.getSize() == null || request.getPeopleQuantity() == null ||
        request.getRooms() == null || request.getBathrooms() == null ||
        request.getCity() == null || request.getCity().isEmpty() ||
        request.getCountry() == null || request.getCountry().isEmpty() ||
        request.getValueNight() == null) {
      logger.warn("Missing required fields for update");
      return false;
    }

    List<Image> existingImages = imageRepository.findByRentalId(rentalId);
    for (Image img : existingImages) {
      imageStorage.deleteImage(img.getId(), img.getExtension());
      imageRepository.delete(img);
    }

    List<MultipartFile> images = request.getImages();
    List<SavedImage> savedImages = new ArrayList<>();

    if (images != null && !images.isEmpty()) {
      if (images.size() > 3) {
        logger.warn("Maximum 3 images allowed, got: {}", images.size());
        return false;
      }

      for (MultipartFile file : images) {
        SavedImage saved = imageStorage.saveImage(file);
        if (saved == null) {
          for (SavedImage s : savedImages) {
            imageStorage.deleteImage(s.getId(), s.getExtension());
          }
          logger.warn("Failed to save image during update, rolling back");
          return false;
        }
        savedImages.add(saved);
      }
    }

    try {
      rental.setName(request.getName());
      rental.setDescription(request.getDescription() != null ? request.getDescription() : "");
      rental.setContact(request.getContact());
      rental.setSize(request.getSize());
      rental.setPeopleQuantity(request.getPeopleQuantity());
      rental.setRooms(request.getRooms());
      rental.setBathrooms(request.getBathrooms());
      rental.setCity(request.getCity().toUpperCase());
      rental.setCountry(request.getCountry().toUpperCase());
      rental.setLocation(request.getLocation());
      rental.setValueNight(request.getValueNight());

      Rental savedRental = rentalRepository.save(rental);

      for (SavedImage saved : savedImages) {
        Image image = new Image(saved.getId(), saved.getExtension(), savedRental);
        imageRepository.save(image);
      }

      logger.info("Rental updated successfully: {}", savedRental.getId());
      return true;

    } catch (Exception e) {
      for (SavedImage saved : savedImages) {
        imageStorage.deleteImage(saved.getId(), saved.getExtension());
      }
      logger.error("Error updating rental: {}", e.getMessage(), e);
      return false;
    }
  }

  public boolean createRental(String token, CreateRentalRequest request) {
    if (!isValidHostToken(token)) {
      logger.warn("Invalid or non-HOST token for createRental");
      return false;
    }

    UUID userId = jwt.getUserId(token);
    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      logger.warn("User not found: {}", userId);
      return false;
    }

    List<MultipartFile> images = request.getImages();
    List<SavedImage> savedImages = new ArrayList<>();

    if (images != null && !images.isEmpty()) {
      if (images.size() > 3) {
        logger.warn("Maximum 3 images allowed, got: {}", images.size());
        return false;
      }

      for (MultipartFile file : images) {
        SavedImage saved = imageStorage.saveImage(file);
        if (saved == null) {
          for (SavedImage s : savedImages) {
            imageStorage.deleteImage(s.getId(), s.getExtension());
          }
          logger.warn("Failed to save image, rolling back all images");
          return false;
        }
        savedImages.add(saved);
      }
    }

    try {
      User user = userOpt.get();
      Rental rental = new Rental(
          user,
          request.getName(),
          request.getDescription() != null ? request.getDescription() : "",
          request.getContact(),
          request.getSize(),
          request.getPeopleQuantity(),
          request.getRooms(),
          request.getBathrooms(),
          request.getCity(),
          request.getCountry(),
          request.getLocation(),
          request.getValueNight());

      Rental savedRental = rentalRepository.save(rental);

      for (SavedImage saved : savedImages) {
        Image image = new Image(saved.getId(), saved.getExtension(), savedRental);
        imageRepository.save(image);
      }

      logger.info("Rental created successfully: {}", savedRental.getId());
      return true;

    } catch (Exception e) {
      for (SavedImage saved : savedImages) {
        imageStorage.deleteImage(saved.getId(), saved.getExtension());
      }
      logger.error("Error creating rental: {}", e.getMessage(), e);
      return false;
    }
  }

  public List<RentalResponse> getRentalsByToken(String token, boolean includeDisabled) {
    if (!jwt.validateToken(token)) {
      logger.warn("Invalid token for getRentalsByToken");
      return List.of();
    }

    UUID userId = jwt.getUserId(token);
    List<Rental> rentals = rentalRepository.findByUserId(userId);

    if (!includeDisabled) {
      rentals = rentals.stream()
          .filter(Rental::isEnable)
          .toList();
    }

    return rentals.stream()
        .map(this::toRentalResponse)
        .toList();
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

  public boolean deleteRental(String token, UUID rentalId) {
    if (!isValidHostOrAdminToken(token)) {
      logger.warn("Invalid token for deleteRental");
      return false;
    }

    UUID userId = jwt.getUserId(token);
    Optional<Rental> rentalOpt = rentalRepository.findById(rentalId);
    if (rentalOpt.isEmpty()) {
      logger.warn("Rental not found: {}", rentalId);
      return false;
    }

    Rental rental = rentalOpt.get();
    String userRole = jwt.getRol(token);

    boolean isOwner = rental.getUser().getId().equals(userId);
    boolean isAdmin = "ADMIN".equals(userRole);

    if (!isOwner && !isAdmin) {
      logger.warn("User {} is not authorized to delete rental {}", userId, rentalId);
      return false;
    }

    List<Reservation> futureReservations = reservationRepository.findByRentalIdAndStartingDateAfterAndIsCancelledFalse(
        rentalId, LocalDate.now());
    if (!futureReservations.isEmpty()) {
      logger.warn("Cannot delete rental {} - has {} future reservation(s)", rentalId, futureReservations.size());
      return false;
    }

    try {
      List<Image> existingImages = imageRepository.findByRentalId(rentalId);
      for (Image img : existingImages) {
        imageStorage.deleteImage(img.getId(), img.getExtension());
        imageRepository.delete(img);
      }

      rentalRepository.delete(rental);
      logger.info("Rental deleted successfully: {}", rentalId);
      return true;

    } catch (Exception e) {
      logger.error("Error deleting rental: {}", e.getMessage(), e);
      return false;
    }
  }

  public boolean setRentalEnabled(String token, UUID rentalId, boolean enabled) {
    if (!isValidHostOrAdminToken(token)) {
      logger.warn("Invalid token for setRentalEnabled");
      return false;
    }

    UUID userId = jwt.getUserId(token);
    Optional<Rental> rentalOpt = rentalRepository.findById(rentalId);
    if (rentalOpt.isEmpty()) {
      logger.warn("Rental not found: {}", rentalId);
      return false;
    }

    Rental rental = rentalOpt.get();
    String userRole = jwt.getRol(token);

    boolean isOwner = rental.getUser().getId().equals(userId);
    boolean isAdmin = "ADMIN".equals(userRole);

    if (!isOwner && !isAdmin) {
      logger.warn("User {} is not authorized to set rental {} enabled status", userId, rentalId);
      return false;
    }

    try {
      rental.setEnable(enabled);
      rentalRepository.save(rental);

      if (enabled) {
        logger.info("Rental enabled successfully: {}", rentalId);
      } else {
        logger.info("Rental disabled successfully: {}", rentalId);
      }
      return true;

    } catch (Exception e) {
      logger.error("Error setting rental enabled status: {}", e.getMessage(), e);
      return false;
    }
  }

  public boolean hasFutureReservations(String token, UUID rentalId) {
    if (!jwt.validateToken(token)) {
      return false;
    }

    Optional<Rental> rentalOpt = rentalRepository.findById(rentalId);
    if (rentalOpt.isEmpty()) {
      return false;
    }

    UUID userId = jwt.getUserId(token);
    Rental rental = rentalOpt.get();
    String userRole = jwt.getRol(token);

    boolean isOwner = rental.getUser().getId().equals(userId);
    boolean isAdmin = "ADMIN".equals(userRole);

    if (!isOwner && !isAdmin) {
      return false;
    }

    List<Reservation> futureReservations = reservationRepository.findByRentalIdAndStartingDateAfterAndIsCancelledFalse(
        rentalId, LocalDate.now());
    return !futureReservations.isEmpty();
  }

  public List<ReservationResponse> getReservationsByRental(String token, UUID rentalId, boolean upcoming) {
    if (!isValidHostOrAdminToken(token)) {
      logger.warn("Invalid token for getReservationsByRental");
      return null;
    }

    Optional<Rental> rentalOpt = rentalRepository.findById(rentalId);
    if (rentalOpt.isEmpty()) {
      logger.warn("Rental not found: {}", rentalId);
      return null;
    }

    Rental rental = rentalOpt.get();
    UUID userId = jwt.getUserId(token);
    String userRole = jwt.getRol(token);

    boolean isOwner = rental.getUser().getId().equals(userId);
    boolean isAdmin = "ADMIN".equals(userRole);

    if (!isOwner && !isAdmin) {
      logger.warn("User {} is not authorized to view reservations for rental {}", userId, rentalId);
      return null;
    }

    List<Reservation> reservations;
    if (upcoming) {
      reservations = reservationRepository.findByRentalIdAndStartingDateAfterAndIsCancelledFalse(
          rentalId, LocalDate.now());
    } else {
      reservations = reservationRepository.findByRentalIdAndStartingDateBeforeAndIsCancelledFalse(
          rentalId, LocalDate.now());
    }

    return reservations.stream()
        .map(res -> new ReservationResponse(
            res.getId(),
            res.getRental().getId(),
            res.getRental().getName(),
            res.getUser().getName(),
            res.getUser().getSurname(),
            res.getStartingDate(),
            res.getEndDate(),
            res.isCancelled()))
        .toList();
  }

  public boolean cancelReservation(String token, UUID reservationId) {
    if (!jwt.validateToken(token)) {
      logger.warn("Invalid token for cancelReservation");
      return false;
    }

    Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
    if (reservationOpt.isEmpty()) {
      logger.warn("Reservation not found: {}", reservationId);
      return false;
    }

    Reservation reservation = reservationOpt.get();
    UUID userId = jwt.getUserId(token);
    String userRole = jwt.getRol(token);

    User rentalOwner = reservation.getRental().getUser();
    boolean isRentalOwner = rentalOwner.getId().equals(userId);
    boolean isAdmin = "ADMIN".equals(userRole);
    boolean isReservationOwner = reservation.getUser().getId().equals(userId);

    if (!isRentalOwner && !isAdmin && !isReservationOwner) {
      logger.warn("User {} is not authorized to cancel reservation {}", userId, reservationId);
      return false;
    }

    if (reservation.isCancelled()) {
      logger.warn("Reservation {} is already cancelled", reservationId);
      return false;
    }

    try {
      reservation.setCancelled(true);
      reservationRepository.save(reservation);
      logger.info("Reservation cancelled successfully: {}", reservationId);
      return true;

    } catch (Exception e) {
      logger.error("Error cancelling reservation: {}", e.getMessage(), e);
      return false;
    }
  }

}
