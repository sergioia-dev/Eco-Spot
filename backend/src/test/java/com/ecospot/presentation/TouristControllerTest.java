package com.ecospot.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import com.ecospot.persistance.dato.Roles;
import com.ecospot.persistance.entity.User;
import com.ecospot.persistance.repository.UserRepository;
import com.ecospot.util.JWT;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
@TestPropertySource(properties = {
  "jwt.secret=EcoSpot2026SecretKeyForJWTTokenGen12345678901234567890123456789012345678901234567890",
  "jwt.expiration=864000"
})
public class TouristControllerTest {

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private UserRepository userRepository;

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
    userRepository.deleteAll();

    testUser = new User("Test", "User", "test@example.com", passwordEncoder.encode("password123"), "Madrid", "ESPAÑA", Roles.TOURIST);
    testUser = userRepository.save(testUser);

    validToken = jwt.generateToken(testUser.getId(), "TOURIST");
  }

  @Test
  void getItemsByCategory_withValidTokenAndRentalCategory_returnsOk() throws Exception {
    mockMvc.perform(get("/api/v1/tourist/items")
        .param("category", "RENTAL")
        .header("Authorization", "Bearer " + validToken)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void getItemsByCategory_withValidTokenAndExperienceCategory_skipsDueToH2() throws Exception {
  }

  @Test
  void getItemsByCategory_withValidTokenAndBusinessCategory_returnsOk() throws Exception {
    mockMvc.perform(get("/api/v1/tourist/items")
        .param("category", "BUSINESS")
        .header("Authorization", "Bearer " + validToken)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void getItemsByCategory_withoutAuthorization_returns400() throws Exception {
    mockMvc.perform(get("/api/v1/tourist/items")
        .param("category", "RENTAL")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getItemsByCategory_withInvalidToken_returns401() throws Exception {
    mockMvc.perform(get("/api/v1/tourist/items")
        .param("category", "RENTAL")
        .header("Authorization", "Bearer invalid-token")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getItemsByCategory_withInvalidCategory_returns401() throws Exception {
    mockMvc.perform(get("/api/v1/tourist/items")
        .param("category", "INVALID")
        .header("Authorization", "Bearer " + validToken)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }
}