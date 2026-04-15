package com.ecospot.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
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
import com.ecospot.persistance.entity.Rental;
import com.ecospot.persistance.entity.User;
import com.ecospot.persistance.repository.ReservationRepository;
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
  private ReservationRepository reservationRepository;

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
    reservationRepository.deleteAll();
    rentalRepository.deleteAll();
    userRepository.deleteAll();

    User hostUser = new User("Host", "User", "host@example.com", passwordEncoder.encode("password123"),
        "Madrid", "ESPAÑA", Roles.HOST);
    hostUser = userRepository.save(hostUser);

    Rental rental = new Rental();
    rental.setName("Test Rental");
    rental.setContact("1234567890");
    rental.setSize(100);
    rental.setPeopleQuantity(4);
    rental.setRooms(2);
    rental.setBathrooms(1);
    rental.setCity("Madrid");
    rental.setCountry("ESPAÑA");
    rental.setValueNight(150.0);
    rental.setUser(hostUser);
    rental.setEnable(true);
    rentalRepository.save(rental);

    testUser = new User("Test", "User", "test@example.com", passwordEncoder.encode("password123"), "Madrid", "ESPAÑA",
        Roles.TOURIST);
    testUser = userRepository.save(testUser);

    validToken = jwt.generateToken(testUser.getId(), "TOURIST");
  }

  @AfterEach
  void tearDown() {
    reservationRepository.deleteAll();
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
  void createReservation_withValidData_returnsCreated() throws Exception {
    Rental rental = rentalRepository.findAll().get(0);

    mockMvc.perform(post("/api/v1/tourist/rentals/" + rental.getId() + "/reservations")
        .header("Authorization", "Bearer " + validToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "startingDate": "2026-05-01",
              "endDate": "2026-05-05"
            }
            """))
        .andExpect(status().isCreated());
  }

  @Test
  void createReservation_withoutAuthorization_returns400() throws Exception {
    Rental rental = rentalRepository.findAll().get(0);

    mockMvc.perform(post("/api/v1/tourist/rentals/" + rental.getId() + "/reservations")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "startingDate": "2026-05-01",
              "endDate": "2026-05-05"
            }
            """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createReservation_withOverlappingDates_returns403() throws Exception {
    Rental rental = rentalRepository.findAll().get(0);

    mockMvc.perform(post("/api/v1/tourist/rentals/" + rental.getId() + "/reservations")
        .header("Authorization", "Bearer " + validToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "startingDate": "2026-06-01",
              "endDate": "2026-06-05"
            }
            """))
        .andExpect(status().isCreated());

    mockMvc.perform(post("/api/v1/tourist/rentals/" + rental.getId() + "/reservations")
        .header("Authorization", "Bearer " + validToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "startingDate": "2026-06-03",
              "endDate": "2026-06-07"
            }
            """))
        .andExpect(status().isForbidden());
  }

  @Test
  void createReservation_withPastDates_returns403() throws Exception {
    Rental rental = rentalRepository.findAll().get(0);

    mockMvc.perform(post("/api/v1/tourist/rentals/" + rental.getId() + "/reservations")
        .header("Authorization", "Bearer " + validToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "startingDate": "2020-05-01",
              "endDate": "2020-05-05"
            }
            """))
        .andExpect(status().isForbidden());
  }
}