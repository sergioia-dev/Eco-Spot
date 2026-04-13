package com.ecospot.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.ecospot.business.dato.Roles;
import com.ecospot.persistance.entity.User;
import com.ecospot.persistance.repository.UserRepository;
import com.ecospot.persistance.repository.RentalRepository;
import com.ecospot.util.JWT;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=EcoSpot2026SecretKeyForJWTTokenGen12345678901234567890123456789012345678901234567890",
    "jwt.expiration=864000"
})
public class UserControllerTest {

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RentalRepository rentalRepository;

  @Autowired
  private JWT jwt;

  @Autowired
  private PasswordEncoder passwordEncoder;

  private MockMvc mockMvc;
  private User testUser;
  private String validToken;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    rentalRepository.deleteAll();
    userRepository.deleteAll();

    testUser = new User("Test", "User", "test@example.com", passwordEncoder.encode("password123"), "Madrid", "ESPAÑA",
        Roles.TOURIST);
    testUser = userRepository.save(testUser);

    validToken = jwt.generateToken(testUser.getId(), "TOURIST");
  }

  @Test
  void getCurrentUser_withValidToken_returnsOk() throws Exception {
    mockMvc.perform(get("/api/v1/users/me")
        .header("Authorization", "Bearer " + validToken)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void getCurrentUser_withoutAuthorization_returns400() throws Exception {
    mockMvc.perform(get("/api/v1/users/me")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getCurrentUser_withInvalidToken_returns401() throws Exception {
    mockMvc.perform(get("/api/v1/users/me")
        .header("Authorization", "Bearer invalid-token")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getCurrentUser_withBusinessUser_returnsOk() throws Exception {
    userRepository.deleteAll();
    User businessUser = new User("Business", "User", "business@example.com", passwordEncoder.encode("password123"),
        "Madrid", "ESPAÑA",
        Roles.BUSINESS);
    businessUser = userRepository.save(businessUser);

    String businessToken = jwt.generateToken(businessUser.getId(), "BUSINESS");

    mockMvc.perform(get("/api/v1/users/me")
        .header("Authorization", "Bearer " + businessToken)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void updateLocation_withValidToken_returnsOk() throws Exception {
    mockMvc.perform(patch("/api/v1/users/location")
        .header("Authorization", "Bearer " + validToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "city": "Barcelona",
              "country": "ESPAÑA"
            }
            """))
        .andExpect(status().isOk());
  }

  @Test
  void updateLocation_withoutAuthorization_returns400() throws Exception {
    mockMvc.perform(patch("/api/v1/users/location")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "city": "Barcelona",
              "country": "ESPAÑA"
            }
            """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateLocation_withInvalidToken_returns401() throws Exception {
    mockMvc.perform(patch("/api/v1/users/location")
        .header("Authorization", "Bearer invalid-token")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "city": "Barcelona",
              "country": "ESPAÑA"
            }
            """))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void updateLocation_withMissingCity_returns400() throws Exception {
    mockMvc.perform(patch("/api/v1/users/location")
        .header("Authorization", "Bearer " + validToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "country": "ESPAÑA"
            }
            """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateLocation_withMissingCountry_returns400() throws Exception {
    mockMvc.perform(patch("/api/v1/users/location")
        .header("Authorization", "Bearer " + validToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "city": "Barcelona"
            }
            """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateLocation_withEmptyCity_returns400() throws Exception {
    mockMvc.perform(patch("/api/v1/users/location")
        .header("Authorization", "Bearer " + validToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "city": "",
              "country": "ESPAÑA"
            }
            """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateLocation_withEmptyCountry_returns400() throws Exception {
    mockMvc.perform(patch("/api/v1/users/location")
        .header("Authorization", "Bearer " + validToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "city": "Barcelona",
              "country": ""
            }
            """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateLocation_withBusinessUser_returnsOk() throws Exception {
    userRepository.deleteAll();
    User businessUser = new User("Business", "User", "business@example.com", passwordEncoder.encode("password123"),
        "Madrid", "ESPAÑA",
        Roles.BUSINESS);
    businessUser = userRepository.save(businessUser);

    String businessToken = jwt.generateToken(businessUser.getId(), "BUSINESS");

    mockMvc.perform(patch("/api/v1/users/location")
        .header("Authorization", "Bearer " + businessToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "city": "Barcelona",
              "country": "ESPAÑA"
            }
            """))
        .andExpect(status().isOk());
  }
}
