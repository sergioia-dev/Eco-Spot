package com.ecospot.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.UUID;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=EcoSpot2026SecretKeyForJWTTokenGen12345678901234567890123456789012345678901234567890",
    "jwt.expiration=864000"
})
public class RentalControllerTest {

  private static final Logger logger = LoggerFactory.getLogger(RentalControllerTest.class);

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
  private User hostUser;
  private String hostToken;
  private User touristUser;
  private String touristToken;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    rentalRepository.deleteAll();
    userRepository.deleteAll();

    hostUser = new User("Host", "User", "host@example.com", passwordEncoder.encode("password123"),
        "Madrid", "ESPAÑA", Roles.HOST);
    hostUser = userRepository.save(hostUser);
    hostToken = jwt.generateToken(hostUser.getId(), "HOST");

    touristUser = new User("Tourist", "User", "tourist@example.com", passwordEncoder.encode("password123"),
        "Barcelona", "ESPAÑA", Roles.TOURIST);
    touristUser = userRepository.save(touristUser);
    touristToken = jwt.generateToken(touristUser.getId(), "TOURIST");
  }

  @AfterEach
  void tearDown() {
    try {
      Path imagesDir = Paths.get("images");
      if (Files.exists(imagesDir)) {
        Files.walk(imagesDir)
            .filter(Files::isRegularFile)
            .forEach(path -> {
              try {
                Files.delete(path);
              } catch (IOException e) {
                logger.warn("Failed to delete test image: {}", path);
              }
            });
      }
    } catch (IOException e) {
      logger.warn("Failed to cleanup test images: {}", e.getMessage());
    }
  }

  @Test
  void createRental_withValidHostTokenAndValidData_returns201() throws Exception {
    MockMultipartFile image1 = new MockMultipartFile(
        "images", "photo1.jpg", "image/jpeg", "fake image content".getBytes());
    MockMultipartFile image2 = new MockMultipartFile(
        "images", "photo2.png", "image/png", "fake image content".getBytes());

    mockMvc.perform(multipart("/api/v1/host/rentals")
        .file(image1)
        .file(image2)
        .param("name", "Beach House")
        .param("description", "Nice house near the beach")
        .param("contact", "1234567890")
        .param("size", "100")
        .param("peopleQuantity", "4")
        .param("rooms", "2")
        .param("bathrooms", "1")
        .param("city", "Miami")
        .param("country", "USA")
        .param("location", "123 Beach St")
        .param("valueNight", "150.00")
        .header("Authorization", "Bearer " + hostToken)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated());
  }

  @Test
  void createRental_withoutAuthorization_returns400() throws Exception {
    MockMultipartFile image1 = new MockMultipartFile(
        "images", "photo1.jpg", "image/jpeg", "fake image content".getBytes());

    mockMvc.perform(multipart("/api/v1/host/rentals")
        .file(image1)
        .param("name", "Beach House")
        .param("description", "Nice house")
        .param("contact", "1234567890")
        .param("size", "100")
        .param("peopleQuantity", "4")
        .param("rooms", "2")
        .param("bathrooms", "1")
        .param("city", "Miami")
        .param("country", "USA")
        .param("valueNight", "150.00")
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createRental_withInvalidToken_returns401() throws Exception {
    MockMultipartFile image1 = new MockMultipartFile(
        "images", "photo1.jpg", "image/jpeg", "fake image content".getBytes());

    mockMvc.perform(multipart("/api/v1/host/rentals")
        .file(image1)
        .param("name", "Beach House")
        .param("description", "Nice house")
        .param("contact", "1234567890")
        .param("size", "100")
        .param("peopleQuantity", "4")
        .param("rooms", "2")
        .param("bathrooms", "1")
        .param("city", "Miami")
        .param("country", "USA")
        .param("valueNight", "150.00")
        .header("Authorization", "Bearer invalid-token")
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void createRental_withTouristToken_returns401() throws Exception {
    MockMultipartFile image1 = new MockMultipartFile(
        "images", "photo1.jpg", "image/jpeg", "fake image content".getBytes());

    mockMvc.perform(multipart("/api/v1/host/rentals")
        .file(image1)
        .param("name", "Beach House")
        .param("description", "Nice house")
        .param("contact", "1234567890")
        .param("size", "100")
        .param("peopleQuantity", "4")
        .param("rooms", "2")
        .param("bathrooms", "1")
        .param("city", "Miami")
        .param("country", "USA")
        .param("valueNight", "150.00")
        .header("Authorization", "Bearer " + touristToken)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void createRental_withMissingRequiredFields_returns400() throws Exception {
    mockMvc.perform(multipart("/api/v1/host/rentals")
        .param("name", "Beach House")
        .param("description", "Nice house")
        .param("city", "Miami")
        .header("Authorization", "Bearer " + hostToken)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createRental_withMoreThan3Images_returns400() throws Exception {
    MockMultipartFile image1 = new MockMultipartFile(
        "images", "photo1.jpg", "image/jpeg", "fake image content".getBytes());
    MockMultipartFile image2 = new MockMultipartFile(
        "images", "photo2.png", "image/png", "fake image content".getBytes());
    MockMultipartFile image3 = new MockMultipartFile(
        "images", "photo3.webp", "image/webp", "fake image content".getBytes());
    MockMultipartFile image4 = new MockMultipartFile(
        "images", "photo4.jpg", "image/jpeg", "fake image content".getBytes());

    mockMvc.perform(multipart("/api/v1/host/rentals")
        .file(image1)
        .file(image2)
        .file(image3)
        .file(image4)
        .param("name", "Beach House")
        .param("description", "Nice house")
        .param("contact", "1234567890")
        .param("size", "100")
        .param("peopleQuantity", "4")
        .param("rooms", "2")
        .param("bathrooms", "1")
        .param("city", "Miami")
        .param("country", "USA")
        .param("valueNight", "150.00")
        .header("Authorization", "Bearer " + hostToken)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createRental_withoutImages_returns201() throws Exception {
    mockMvc.perform(multipart("/api/v1/host/rentals")
        .param("name", "Beach House")
        .param("description", "Nice house")
        .param("contact", "1234567890")
        .param("size", "100")
        .param("peopleQuantity", "4")
        .param("rooms", "2")
        .param("bathrooms", "1")
        .param("city", "Miami")
        .param("country", "USA")
        .param("location", "123 Beach St")
        .param("valueNight", "150.00")
        .header("Authorization", "Bearer " + hostToken)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated());
  }

  private String createRentalAndGetId(String token) throws Exception {
    mockMvc.perform(multipart("/api/v1/host/rentals")
        .param("name", "Test Rental")
        .param("description", "Test description")
        .param("contact", "1234567890")
        .param("size", "100")
        .param("peopleQuantity", "4")
        .param("rooms", "2")
        .param("bathrooms", "1")
        .param("city", "Miami")
        .param("country", "USA")
        .param("valueNight", "150.00")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated());

    return rentalRepository.findAll().get(0).getId().toString();
  }

  @Test
  void updateRental_withValidHostTokenAndOwnership_returns200() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    mockMvc.perform(put("/api/v1/host/rentals/" + rentalId)
        .param("name", "Updated Beach House")
        .param("description", "Updated description")
        .param("contact", "0987654321")
        .param("size", "150")
        .param("peopleQuantity", "6")
        .param("rooms", "3")
        .param("bathrooms", "2")
        .param("city", "Orlando")
        .param("country", "USA")
        .param("valueNight", "200.00")
        .header("Authorization", "Bearer " + hostToken)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk());
  }

  @Test
  void updateRental_withoutAuthorization_returns400() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    mockMvc.perform(put("/api/v1/host/rentals/" + rentalId)
        .param("name", "Updated Beach House")
        .param("description", "Updated description")
        .param("contact", "0987654321")
        .param("size", "150")
        .param("peopleQuantity", "6")
        .param("rooms", "3")
        .param("bathrooms", "2")
        .param("city", "Orlando")
        .param("country", "USA")
        .param("valueNight", "200.00")
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateRental_withInvalidToken_returns403() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    mockMvc.perform(put("/api/v1/host/rentals/" + rentalId)
        .param("name", "Updated Beach House")
        .param("description", "Updated description")
        .param("contact", "0987654321")
        .param("size", "150")
        .param("peopleQuantity", "6")
        .param("rooms", "3")
        .param("bathrooms", "2")
        .param("city", "Orlando")
        .param("country", "USA")
        .param("valueNight", "200.00")
        .header("Authorization", "Bearer invalid-token")
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isForbidden());
  }

  @Test
  void updateRental_withTouristToken_returns403() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    mockMvc.perform(put("/api/v1/host/rentals/" + rentalId)
        .param("name", "Updated Beach House")
        .param("description", "Updated description")
        .param("contact", "0987654321")
        .param("size", "150")
        .param("peopleQuantity", "6")
        .param("rooms", "3")
        .param("bathrooms", "2")
        .param("city", "Orlando")
        .param("country", "USA")
        .param("valueNight", "200.00")
        .header("Authorization", "Bearer " + touristToken)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isForbidden());
  }

  @Test
  void updateRental_withHostTokenButNotOwner_returns403() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    User otherHost = new User("Other", "Host", "other@example.com", passwordEncoder.encode("password123"),
        "Madrid", "ESPAÑA", Roles.HOST);
    otherHost = userRepository.save(otherHost);
    String otherHostToken = jwt.generateToken(otherHost.getId(), "HOST");

    mockMvc.perform(put("/api/v1/host/rentals/" + rentalId)
        .param("name", "Updated Beach House")
        .param("description", "Updated description")
        .param("contact", "0987654321")
        .param("size", "150")
        .param("peopleQuantity", "6")
        .param("rooms", "3")
        .param("bathrooms", "2")
        .param("city", "Orlando")
        .param("country", "USA")
        .param("valueNight", "200.00")
        .header("Authorization", "Bearer " + otherHostToken)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isForbidden());
  }

  @Test
  void updateRental_withNonExistentRentalId_returns403() throws Exception {
    String nonExistentId = UUID.randomUUID().toString();

    mockMvc.perform(put("/api/v1/host/rentals/" + nonExistentId)
        .param("name", "Updated Beach House")
        .param("description", "Updated description")
        .param("contact", "0987654321")
        .param("size", "150")
        .param("peopleQuantity", "6")
        .param("rooms", "3")
        .param("bathrooms", "2")
        .param("city", "Orlando")
        .param("country", "USA")
        .param("valueNight", "200.00")
        .header("Authorization", "Bearer " + hostToken)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isForbidden());
  }

  @Test
  void updateRental_withMissingRequiredFields_returns400() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    mockMvc.perform(put("/api/v1/host/rentals/" + rentalId)
        .param("name", "Updated Beach House")
        .param("description", "Updated description")
        .header("Authorization", "Bearer " + hostToken)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateRental_withMoreThan3Images_returns400() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    MockMultipartFile image1 = new MockMultipartFile(
        "images", "photo1.jpg", "image/jpeg", "fake image content".getBytes());
    MockMultipartFile image2 = new MockMultipartFile(
        "images", "photo2.png", "image/png", "fake image content".getBytes());
    MockMultipartFile image3 = new MockMultipartFile(
        "images", "photo3.webp", "image/webp", "fake image content".getBytes());
    MockMultipartFile image4 = new MockMultipartFile(
        "images", "photo4.jpg", "image/jpeg", "fake image content".getBytes());

    mockMvc.perform(multipart(HttpMethod.PUT, "/api/v1/host/rentals/" + rentalId)
        .file(image1)
        .file(image2)
        .file(image3)
        .file(image4)
        .param("name", "Updated Beach House")
        .param("description", "Updated description")
        .param("contact", "0987654321")
        .param("size", "150")
        .param("peopleQuantity", "6")
        .param("rooms", "3")
        .param("bathrooms", "2")
        .param("city", "Orlando")
        .param("country", "USA")
        .param("valueNight", "200.00")
        .header("Authorization", "Bearer " + hostToken)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateRental_withoutImages_returns200() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    mockMvc.perform(put("/api/v1/host/rentals/" + rentalId)
        .param("name", "Updated Beach House")
        .param("description", "Updated description")
        .param("contact", "0987654321")
        .param("size", "150")
        .param("peopleQuantity", "6")
        .param("rooms", "3")
        .param("bathrooms", "2")
        .param("city", "Orlando")
        .param("country", "USA")
        .param("valueNight", "200.00")
        .header("Authorization", "Bearer " + hostToken)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk());
  }

}