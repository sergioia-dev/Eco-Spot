package com.ecospot.business.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ecospot.business.dato.BusinessResponse;
import com.ecospot.business.dato.BusinessResponse.ImageInfo;
import com.ecospot.business.dato.CreateBusinessRequest;
import com.ecospot.persistance.entity.Business;
import com.ecospot.persistance.entity.Image;
import com.ecospot.persistance.entity.User;
import com.ecospot.persistance.repository.BusinessRepository;
import com.ecospot.persistance.repository.ImageRepository;
import com.ecospot.persistance.repository.UserRepository;
import com.ecospot.util.ImageStorage;
import com.ecospot.util.ImageStorage.SavedImage;
import com.ecospot.util.JWT;

@Service
public class BusinessService {
  private static final Logger logger = LoggerFactory.getLogger(BusinessService.class);

  private final JWT jwt;
  private final UserRepository userRepository;
  private final BusinessRepository businessRepository;
  private final ImageRepository imageRepository;
  private final ImageStorage imageStorage;

  public BusinessService(JWT jwt, UserRepository userRepository, BusinessRepository businessRepository,
      ImageRepository imageRepository, ImageStorage imageStorage) {
    this.jwt = jwt;
    this.userRepository = userRepository;
    this.businessRepository = businessRepository;
    this.imageRepository = imageRepository;
    this.imageStorage = imageStorage;
  }

  private boolean isValidBusinessToken(String token) {
    if (!jwt.validateToken(token)) {
      logger.warn("Invalid token provided for business operation");
      return false;
    }
    return "BUSINESS".equals(jwt.getRol(token));
  }

  private boolean isValidBusinessOrAdminToken(String token) {
    if (!jwt.validateToken(token)) {
      logger.warn("Invalid token provided for business operation");
      return false;
    }
    String role = jwt.getRol(token);
    return "BUSINESS".equals(role) || "ADMIN".equals(role);
  }

  public boolean createBusiness(String token, CreateBusinessRequest request) {
    if (!isValidBusinessToken(token)) {
      logger.warn("Invalid or non-BUSINESS token for createBusiness");
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
      Business business = new Business(
          user,
          request.getName(),
          request.getDescription() != null ? request.getDescription() : "",
          request.getContact(),
          request.getCity(),
          request.getCountry(),
          request.getLocation(),
          request.getMenu());

      Business savedBusiness = businessRepository.save(business);

      for (SavedImage saved : savedImages) {
        Image image = new Image(saved.getId(), saved.getExtension(), savedBusiness);
        imageRepository.save(image);
      }

      logger.info("Business created successfully: {}", savedBusiness.getId());
      return true;

    } catch (Exception e) {
      for (SavedImage saved : savedImages) {
        imageStorage.deleteImage(saved.getId(), saved.getExtension());
      }
      logger.error("Error creating business: {}", e.getMessage(), e);
      return false;
    }
  }

  public boolean updateBusiness(String token, UUID businessId, CreateBusinessRequest request) {
    if (!isValidBusinessOrAdminToken(token)) {
      logger.warn("Invalid token for updateBusiness");
      return false;
    }

    UUID userId = jwt.getUserId(token);
    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      logger.warn("User not found: {}", userId);
      return false;
    }

    Optional<Business> businessOpt = businessRepository.findById(businessId);
    if (businessOpt.isEmpty()) {
      logger.warn("Business not found: {}", businessId);
      return false;
    }

    Business business = businessOpt.get();
    String userRole = jwt.getRol(token);

    boolean isOwner = business.getUser().getId().equals(userId);
    boolean isAdmin = "ADMIN".equals(userRole);

    if (!isOwner && !isAdmin) {
      logger.warn("User {} is not authorized to update business {}", userId, businessId);
      return false;
    }

    if (request.getName() == null || request.getName().isEmpty() ||
        request.getContact() == null || request.getContact().isEmpty() ||
        request.getCity() == null || request.getCity().isEmpty() ||
        request.getCountry() == null || request.getCountry().isEmpty()) {
      logger.warn("Missing required fields for update");
      return false;
    }

    List<Image> existingImages = imageRepository.findByBusinessId(businessId);
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
      business.setName(request.getName());
      business.setDescription(request.getDescription() != null ? request.getDescription() : "");
      business.setContact(request.getContact());
      business.setCity(request.getCity().toUpperCase());
      business.setCountry(request.getCountry().toUpperCase());
      business.setLocation(request.getLocation());
      business.setMenu(request.getMenu());

      Business savedBusiness = businessRepository.save(business);

      for (SavedImage saved : savedImages) {
        Image image = new Image(saved.getId(), saved.getExtension(), savedBusiness);
        imageRepository.save(image);
      }

      logger.info("Business updated successfully: {}", savedBusiness.getId());
      return true;

    } catch (Exception e) {
      for (SavedImage saved : savedImages) {
        imageStorage.deleteImage(saved.getId(), saved.getExtension());
      }
      logger.error("Error updating business: {}", e.getMessage(), e);
      return false;
    }
  }

  public boolean deleteBusiness(String token, UUID businessId) {
    if (!isValidBusinessOrAdminToken(token)) {
      logger.warn("Invalid token for deleteBusiness");
      return false;
    }

    UUID userId = jwt.getUserId(token);
    Optional<Business> businessOpt = businessRepository.findById(businessId);
    if (businessOpt.isEmpty()) {
      logger.warn("Business not found: {}", businessId);
      return false;
    }

    Business business = businessOpt.get();
    String userRole = jwt.getRol(token);

    boolean isOwner = business.getUser().getId().equals(userId);
    boolean isAdmin = "ADMIN".equals(userRole);

    if (!isOwner && !isAdmin) {
      logger.warn("User {} is not authorized to delete business {}", userId, businessId);
      return false;
    }

    try {
      List<Image> existingImages = imageRepository.findByBusinessId(businessId);
      for (Image img : existingImages) {
        imageStorage.deleteImage(img.getId(), img.getExtension());
        imageRepository.delete(img);
      }

      businessRepository.delete(business);
      logger.info("Business deleted successfully: {}", businessId);
      return true;

    } catch (Exception e) {
      logger.error("Error deleting business: {}", e.getMessage(), e);
      return false;
    }
  }

  public boolean setBusinessEnabled(String token, UUID businessId, boolean enabled) {
    if (!isValidBusinessOrAdminToken(token)) {
      logger.warn("Invalid token for setBusinessEnabled");
      return false;
    }

    UUID userId = jwt.getUserId(token);
    Optional<Business> businessOpt = businessRepository.findById(businessId);
    if (businessOpt.isEmpty()) {
      logger.warn("Business not found: {}", businessId);
      return false;
    }

    Business business = businessOpt.get();
    String userRole = jwt.getRol(token);

    boolean isOwner = business.getUser().getId().equals(userId);
    boolean isAdmin = "ADMIN".equals(userRole);

    if (!isOwner && !isAdmin) {
      logger.warn("User {} is not authorized to set business {} enabled status", userId, businessId);
      return false;
    }

    try {
      business.setEnable(enabled);
      businessRepository.save(business);

      if (enabled) {
        logger.info("Business enabled successfully: {}", businessId);
      } else {
        logger.info("Business disabled successfully: {}", businessId);
      }
      return true;

    } catch (Exception e) {
      logger.error("Error setting business enabled status: {}", e.getMessage(), e);
      return false;
    }
  }

  public List<BusinessResponse> getBusinessesByToken(String token, boolean includeDisabled) {
    if (!jwt.validateToken(token)) {
      logger.warn("Invalid token for getBusinessesByToken");
      return List.of();
    }

    UUID userId = jwt.getUserId(token);
    List<Business> businesses = businessRepository.findByUserId(userId);

    if (!includeDisabled) {
      businesses = businesses.stream()
          .filter(Business::isEnable)
          .toList();
    }

    return businesses.stream()
        .map(this::toBusinessResponse)
        .toList();
  }

  private BusinessResponse toBusinessResponse(Business business) {
    List<ImageInfo> images = imageRepository.findByBusinessId(business.getId()).stream()
        .map(img -> new ImageInfo(img.getId(), img.getExtension()))
        .toList();

    return new BusinessResponse(
        business.getId(),
        business.getName(),
        business.getDescription(),
        business.getContact(),
        business.getCity(),
        business.getCountry(),
        business.getLocation(),
        business.getMenu(),
        business.isEnable(),
        images);
  }

}