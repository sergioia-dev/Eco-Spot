package com.ecospot.business.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ecospot.persistance.entity.Business;
import com.ecospot.persistance.entity.Experience;
import com.ecospot.business.dato.ItemCategory;
import com.ecospot.persistance.entity.Rental;
import com.ecospot.persistance.entity.User;
import com.ecospot.persistance.repository.BusinessRepository;
import com.ecospot.persistance.repository.ExperienceRepository;
import com.ecospot.persistance.repository.RentalRepository;
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

  public TouristService(JWT jwt, RentalRepository rentalRepository, BusinessRepository businessRepository,
      ExperienceRepository experienceRepository, UserRepository userRepository) {
    this.jwt = jwt;
    this.rentalRepository = rentalRepository;
    this.businessRepository = businessRepository;
    this.experienceRepository = experienceRepository;
    this.userRepository = userRepository;
  }

  private boolean isValidTouristToken(String token) {
    if (!jwt.validateToken(token)) {
      return false;
    }
    return "TOURIST".equals(jwt.getRol(token));
  }

  public Object getItemsByCategory(String token, ItemCategory category) {
    if (!isValidTouristToken(token)) {
      logger.warn("Invalid token provided for getItemsByCategory");
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

    return switch (category) {
      case RENTAL -> rentalRepository.findByCityAndCountry(city, country);
      case BUSINESS -> businessRepository.findByCityAndCountry(city, country);
      case EXPERIENCE -> experienceRepository.findByCityAndCountry(city, country);
    };
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

  public boolean updateLocation(String token, String city, String country) {
    if (!isValidTouristToken(token)) {
      logger.warn("Invalid token provided for updateLocation");
      return false;
    }

    Optional<User> userOpt = getUserFromToken(token);
    if (userOpt.isEmpty()) {
      logger.warn("User not found: {}", jwt.getUserId(token));
      return false;
    }

    User user = userOpt.get();
    user.setCurrentCity(city != null ? city.toUpperCase() : null);
    user.setCurrentCountry(country != null ? country.toUpperCase() : null);

    try {
      userRepository.save(user);
      logger.info("User location updated: {}", user.getId());
      return true;
    } catch (Exception e) {
      logger.error("Error updating user location: {}", e.getMessage(), e);
      return false;
    }
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

}
