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
public class TouristControllerTest {

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
  void getItems_withValidToken_returnsOk() throws Exception {
    mockMvc.perform(get("/api/v1/tourist/items")
        .header("Authorization", "Bearer " + validToken)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void getItems_withoutAuthorization_returns400() throws Exception {
    mockMvc.perform(get("/api/v1/tourist/items")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getItems_withInvalidToken_returns401() throws Exception {
    mockMvc.perform(get("/api/v1/tourist/items")
        .header("Authorization", "Bearer invalid-token")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void search_withValidTokenAndNoCategory_returnsOk() throws Exception {
    mockMvc.perform(get("/api/v1/tourist/search")
        .param("searchBy", "test")
        .header("Authorization", "Bearer " + validToken)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void search_withValidTokenAndRentalCategory_returnsOk() throws Exception {
    mockMvc.perform(get("/api/v1/tourist/search")
        .param("category", "RENTAL")
        .param("searchBy", "test")
        .header("Authorization", "Bearer " + validToken)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void search_withValidTokenAndBusinessCategory_returnsOk() throws Exception {
    mockMvc.perform(get("/api/v1/tourist/search")
        .param("category", "BUSINESS")
        .param("searchBy", "test")
        .header("Authorization", "Bearer " + validToken)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void search_withValidTokenAndExperienceCategory_returnsOk() throws Exception {
    mockMvc.perform(get("/api/v1/tourist/search")
        .param("category", "EXPERIENCE")
        .param("searchBy", "test")
        .header("Authorization", "Bearer " + validToken)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void search_withoutAuthorizationHeader_returns400() throws Exception {
    mockMvc.perform(get("/api/v1/tourist/search")
        .param("searchBy", "test")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void search_withInvalidToken_returns401() throws Exception {
    mockMvc.perform(get("/api/v1/tourist/search")
        .param("searchBy", "test")
        .header("Authorization", "Bearer invalid-token")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void search_withInvalidCategory_returns400() throws Exception {
    mockMvc.perform(get("/api/v1/tourist/search")
        .param("category", "INVALID")
        .param("searchBy", "test")
        .header("Authorization", "Bearer " + validToken)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void search_withEmptySearchBy_returns400() throws Exception {
    mockMvc.perform(get("/api/v1/tourist/search")
        .param("searchBy", "")
        .header("Authorization", "Bearer " + validToken)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void search_withoutSearchByParam_returns400() throws Exception {
    mockMvc.perform(get("/api/v1/tourist/search")
        .header("Authorization", "Bearer " + validToken)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }
}
