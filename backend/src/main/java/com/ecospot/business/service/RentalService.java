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
import com.ecospot.business.dato.UpdateRentalRequest;
import com.ecospot.persistance.entity.Image;
import com.ecospot.persistance.entity.Rental;
import com.ecospot.persistance.entity.User;
import com.ecospot.persistance.repository.ImageRepository;
import com.ecospot.persistance.repository.RentalRepository;
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

  public RentalService(JWT jwt, UserRepository userRepository, RentalRepository rentalRepository,
      ImageRepository imageRepository, ImageStorage imageStorage) {
    this.jwt = jwt;
    this.userRepository = userRepository;
    this.rentalRepository = rentalRepository;
    this.imageRepository = imageRepository;
    this.imageStorage = imageStorage;
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

}
