package com.ecospot.business;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecospot.persistance.entity.Roles;
import com.ecospot.persistance.entity.User;
import com.ecospot.persistance.repository.UserRepository;
import com.ecospot.util.JWT;

@Service
public class AuthService {

  private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JWT jwt;

  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JWT jwt) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwt = jwt;
  }

  public boolean createUser(String name, String surname, String email, String password, String rol) {
    try {
      if (userRepository.existsByEmail(email)) {
        logger.warn("User with email {} already exists", email);
        return false;
      }
      User user = new User(name, surname, email, passwordEncoder.encode(password), Roles.valueOf(rol));
      userRepository.save(user);
      logger.info("User created successfully: {}", email);
      return true;
    } catch (Exception e) {
      logger.error("Error creating user: {}", e.getMessage(), e);
      return false;
    }
  }

  public String login(String email, String password) {
    Optional<User> userOpt = userRepository.findByEmail(email);
    if (userOpt.isEmpty()) {
      logger.warn("Login failed: user not found {}", email);
      return null;
    }
    User user = userOpt.get();
    if (!passwordEncoder.matches(password, user.getPassword())) {
      logger.warn("Login failed: wrong password for {}", email);
      return null;
    }
    String token = jwt.generateToken(user.getId(), user.getRol().name());
    logger.info("User logged in: {}", email);
    return token;
  }

  public boolean validateToken(String token) {
    return jwt.validateToken(token);
  }

}
