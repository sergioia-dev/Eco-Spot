package com.ecospot.business;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

  public Object getItemsByCategory(String token, String category) {
    if (!isValidTouristToken(token)) {
      logger.warn("Invalid token provided for getItemsByCategory");
      return null;
    }

    UUID userId = jwt.getUserId(token);
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      logger.warn("User not found: {}", userId);
      return null;
    }

    String city = user.get().getCurrentCity();
    String country = user.get().getCurrentCountry();

    if (city != null && country != null) {
      city = city.toUpperCase();
      country = country.toUpperCase();
    }

    return switch (category.toUpperCase()) {
      case "RENTAL" -> rentalRepository.findByCityAndCountry(city, country);
      case "BUSINESS" -> businessRepository.findByCityAndCountry(city, country);
      case "EXPERIENCE" -> experienceRepository.findByCityAndCountry(city, country);
      default -> {
        logger.warn("Invalid category: {}", category);
        yield null;
      }
    };
  }

}
