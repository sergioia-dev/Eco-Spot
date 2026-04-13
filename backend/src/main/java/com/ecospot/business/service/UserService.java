package com.ecospot.business.service;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ecospot.persistance.entity.User;
import com.ecospot.persistance.repository.UserRepository;
import com.ecospot.util.JWT;

@Service
public class UserService {
  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  private final JWT jwt;
  private final UserRepository userRepository;

  public UserService(JWT jwt, UserRepository userRepository) {
    this.jwt = jwt;
    this.userRepository = userRepository;
  }

  public boolean updateLocation(String token, String city, String country) {
    if (!jwt.validateToken(token)) {
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

}