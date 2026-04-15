package com.ecospot.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
import com.ecospot.persistance.entity.Rental;
import com.ecospot.persistance.entity.Reservation;
import com.ecospot.persistance.entity.User;
import com.ecospot.persistance.repository.ReservationRepository;
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
  private ReservationRepository reservationRepository;

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
    reservationRepository.deleteAll();
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

  @Test
  void deleteRental_withValidHostTokenAndOwnership_returns200() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    mockMvc.perform(delete("/api/v1/host/rentals/" + rentalId)
        .header("Authorization", "Bearer " + hostToken))
        .andExpect(status().isOk());
  }

  @Test
  void deleteRental_withoutAuthorization_returns400() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    mockMvc.perform(delete("/api/v1/host/rentals/" + rentalId))
        .andExpect(status().isBadRequest());
  }

  @Test
  void deleteRental_withInvalidToken_returns403() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    mockMvc.perform(delete("/api/v1/host/rentals/" + rentalId)
        .header("Authorization", "Bearer invalid-token"))
        .andExpect(status().isForbidden());
  }

  @Test
  void deleteRental_withTouristToken_returns403() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    mockMvc.perform(delete("/api/v1/host/rentals/" + rentalId)
        .header("Authorization", "Bearer " + touristToken))
        .andExpect(status().isForbidden());
  }

  @Test
  void deleteRental_withHostTokenButNotOwner_returns403() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    User otherHost = new User("Other", "Host", "other@example.com", passwordEncoder.encode("password123"),
        "Madrid", "ESPAÑA", Roles.HOST);
    otherHost = userRepository.save(otherHost);
    String otherHostToken = jwt.generateToken(otherHost.getId(), "HOST");

    mockMvc.perform(delete("/api/v1/host/rentals/" + rentalId)
        .header("Authorization", "Bearer " + otherHostToken))
        .andExpect(status().isForbidden());
  }

  @Test
  void deleteRental_withNonExistentRentalId_returns403() throws Exception {
    String nonExistentId = UUID.randomUUID().toString();

    mockMvc.perform(delete("/api/v1/host/rentals/" + nonExistentId)
        .header("Authorization", "Bearer " + hostToken))
        .andExpect(status().isForbidden());
  }

  @Test
  void deleteRental_withFutureReservation_returns409() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);
    Rental rental = rentalRepository.findById(UUID.fromString(rentalId)).get();

Reservation reservation = new Reservation(
        hostUser,
        rental,
        LocalDate.now().plusDays(1),
        LocalDate.now().plusDays(3));
    reservationRepository.save(reservation);

    mockMvc.perform(delete("/api/v1/host/rentals/" + rentalId)
        .header("Authorization", "Bearer " + hostToken))
        .andExpect(status().isConflict());
  }

  @Test
  void getRentals_withValidHostToken_returnsEnabledRentals() throws Exception {
    createRentalAndGetId(hostToken);

    mockMvc.perform(get("/api/v1/host/rentals")
        .header("Authorization", "Bearer " + hostToken))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json"));
  }

  @Test
  void getRentals_withoutAuthorization_returns400() throws Exception {
    mockMvc.perform(get("/api/v1/host/rentals"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getRentals_withIncludeDisabled_returnsAllRentals() throws Exception {
    createRentalAndGetId(hostToken);

    mockMvc.perform(get("/api/v1/host/rentals")
        .param("includeDisabled", "true")
        .header("Authorization", "Bearer " + hostToken))
        .andExpect(status().isOk());
  }

  @Test
  void getRentals_withInvalidToken_returnsEmptyList() throws Exception {
    mockMvc.perform(get("/api/v1/host/rentals")
        .header("Authorization", "Bearer invalid-token"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));
  }

  @Test
  void setRentalEnabled_withValidHostTokenAndOwnership_enablesRental() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    mockMvc.perform(patch("/api/v1/host/rentals/" + rentalId + "/enable")
        .param("enabled", "true")
        .header("Authorization", "Bearer " + hostToken))
        .andExpect(status().isOk());
  }

  @Test
  void setRentalEnabled_withValidHostToken_disablesRental() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    mockMvc.perform(patch("/api/v1/host/rentals/" + rentalId + "/enable")
        .param("enabled", "false")
        .header("Authorization", "Bearer " + hostToken))
        .andExpect(status().isOk());
  }

  @Test
  void setRentalEnabled_withoutAuthorization_returns400() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    mockMvc.perform(patch("/api/v1/host/rentals/" + rentalId + "/enable")
        .param("enabled", "true"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void setRentalEnabled_withInvalidToken_returns403() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    mockMvc.perform(patch("/api/v1/host/rentals/" + rentalId + "/enable")
        .param("enabled", "true")
        .header("Authorization", "Bearer invalid-token"))
        .andExpect(status().isForbidden());
  }

  @Test
  void setRentalEnabled_withTouristToken_returns403() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    mockMvc.perform(patch("/api/v1/host/rentals/" + rentalId + "/enable")
        .param("enabled", "true")
        .header("Authorization", "Bearer " + touristToken))
        .andExpect(status().isForbidden());
  }

  @Test
  void setRentalEnabled_withHostTokenButNotOwner_returns403() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    User otherHost = new User("Other", "Host", "other@example.com", passwordEncoder.encode("password123"),
        "Madrid", "ESPAÑA", Roles.HOST);
    otherHost = userRepository.save(otherHost);
    String otherHostToken = jwt.generateToken(otherHost.getId(), "HOST");

    mockMvc.perform(patch("/api/v1/host/rentals/" + rentalId + "/enable")
        .param("enabled", "true")
        .header("Authorization", "Bearer " + otherHostToken))
        .andExpect(status().isForbidden());
  }

  @Test
  void getReservations_withValidHostTokenAndOwnership_returnsUpcoming() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    mockMvc.perform(get("/api/v1/host/rentals/" + rentalId + "/reservations")
        .param("upcoming", "true")
        .header("Authorization", "Bearer " + hostToken))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json"));
  }

  @Test
  void getReservations_withoutAuthorization_returns400() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    mockMvc.perform(get("/api/v1/host/rentals/" + rentalId + "/reservations")
        .param("upcoming", "true"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getReservations_withInvalidToken_returns403() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    mockMvc.perform(get("/api/v1/host/rentals/" + rentalId + "/reservations")
        .param("upcoming", "true")
        .header("Authorization", "Bearer invalid-token"))
        .andExpect(status().isForbidden());
  }

  @Test
  void getReservations_withTouristToken_returns403() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    mockMvc.perform(get("/api/v1/host/rentals/" + rentalId + "/reservations")
        .param("upcoming", "true")
        .header("Authorization", "Bearer " + touristToken))
        .andExpect(status().isForbidden());
  }

  @Test
  void getReservations_withHostTokenButNotOwner_returns403() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    User otherHost = new User("Other", "Host", "other@example.com", passwordEncoder.encode("password123"),
        "Madrid", "ESPAÑA", Roles.HOST);
    otherHost = userRepository.save(otherHost);
    String otherHostToken = jwt.generateToken(otherHost.getId(), "HOST");

    mockMvc.perform(get("/api/v1/host/rentals/" + rentalId + "/reservations")
        .param("upcoming", "true")
        .header("Authorization", "Bearer " + otherHostToken))
        .andExpect(status().isForbidden());
  }

  @Test
  void getReservations_withPassed_returnsPassedReservations() throws Exception {
    String rentalId = createRentalAndGetId(hostToken);

    mockMvc.perform(get("/api/v1/host/rentals/" + rentalId + "/reservations")
        .param("upcoming", "false")
        .header("Authorization", "Bearer " + hostToken))
        .andExpect(status().isOk());
  }

  private String createReservationAndGetId(String hostToken) throws Exception {
    String rentalId = createRentalAndGetId(hostToken);
    Rental rental = rentalRepository.findById(UUID.fromString(rentalId)).get();

    Reservation reservation = new Reservation(
        touristUser,
        rental,
        LocalDate.now().plusDays(1),
        LocalDate.now().plusDays(3));
    reservationRepository.save(reservation);

    return reservation.getId().toString();
  }

  @Test
  void cancelReservation_withValidHostTokenAndOwnership_cancelsReservation() throws Exception {
    String reservationId = createReservationAndGetId(hostToken);

    mockMvc.perform(patch("/api/v1/host/reservations/" + reservationId + "/cancel")
        .header("Authorization", "Bearer " + hostToken))
        .andExpect(status().isOk());
  }

  @Test
  void cancelReservation_withReservationOwnerTourist_cancelsReservation() throws Exception {
    String reservationId = createReservationAndGetId(hostToken);

    mockMvc.perform(patch("/api/v1/host/reservations/" + reservationId + "/cancel")
        .header("Authorization", "Bearer " + touristToken))
        .andExpect(status().isOk());
  }

  @Test
  void cancelReservation_withoutAuthorization_returns400() throws Exception {
    String reservationId = createReservationAndGetId(hostToken);

    mockMvc.perform(patch("/api/v1/host/reservations/" + reservationId + "/cancel"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void cancelReservation_withInvalidToken_returns403() throws Exception {
    String reservationId = createReservationAndGetId(hostToken);

    mockMvc.perform(patch("/api/v1/host/reservations/" + reservationId + "/cancel")
        .header("Authorization", "Bearer invalid-token"))
        .andExpect(status().isForbidden());
  }

  @Test
  void cancelReservation_withOtherHost_returns403() throws Exception {
    String reservationId = createReservationAndGetId(hostToken);

    User otherHost = new User("Other", "Host", "other@example.com", passwordEncoder.encode("password123"),
        "Madrid", "ESPAÑA", Roles.HOST);
    otherHost = userRepository.save(otherHost);
    String otherHostToken = jwt.generateToken(otherHost.getId(), "HOST");

    mockMvc.perform(patch("/api/v1/host/reservations/" + reservationId + "/cancel")
        .header("Authorization", "Bearer " + otherHostToken))
        .andExpect(status().isForbidden());
  }

  @Test
  void cancelReservation_withAlreadyCancelled_returns403() throws Exception {
    String reservationId = createReservationAndGetId(hostToken);

    mockMvc.perform(patch("/api/v1/host/reservations/" + reservationId + "/cancel")
        .header("Authorization", "Bearer " + hostToken))
        .andExpect(status().isOk());

    mockMvc.perform(patch("/api/v1/host/reservations/" + reservationId + "/cancel")
        .header("Authorization", "Bearer " + hostToken))
        .andExpect(status().isForbidden());
  }

}