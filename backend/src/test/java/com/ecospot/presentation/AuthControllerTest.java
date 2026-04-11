package com.ecospot.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
@TestPropertySource(properties = {
  "jwt.secret=EcoSpot2026SecretKeyForJWTTokenGen12345678901234567890123456789012345678901234567890",
  "jwt.expiration=864000"
})
public class AuthControllerTest {

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    userRepository.deleteAll();
  }

  @Test
  void register_withValidData_returnsCreated() throws Exception {
    mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "name": "John",
              "surname": "Doe",
              "email": "john@example.com",
              "password": "password123",
              "rol": "TOURIST"
            }
            """))
        .andExpect(status().isCreated());
  }

  @Test
  void register_withDuplicateEmail_returnsConflict() throws Exception {
    User existingUser = new User("John", "Doe", "john@example.com", passwordEncoder.encode("password123"), Roles.TOURIST);
    userRepository.save(existingUser);

    mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "name": "Jane",
              "surname": "Doe",
              "email": "john@example.com",
              "password": "password456",
              "rol": "TOURIST"
            }
            """))
        .andExpect(status().isConflict());
  }

  @Test
  void login_withValidCredentials_returnsOk() throws Exception {
    User user = new User("John", "Doe", "john@example.com", passwordEncoder.encode("password123"), Roles.TOURIST);
    userRepository.save(user);

    mockMvc.perform(post("/api/v1/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "email": "john@example.com",
              "password": "password123"
            }
            """))
        .andExpect(status().isOk());
  }

  @Test
  void login_withInvalidEmail_returnsUnauthorized() throws Exception {
    mockMvc.perform(post("/api/v1/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "email": "nonexistent@example.com",
              "password": "password123"
            }
            """))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void login_withInvalidPassword_returnsUnauthorized() throws Exception {
    User user = new User("John", "Doe", "john@example.com", passwordEncoder.encode("password123"), Roles.TOURIST);
    userRepository.save(user);

    mockMvc.perform(post("/api/v1/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "email": "john@example.com",
              "password": "wrongpassword"
            }
            """))
        .andExpect(status().isUnauthorized());
  }
}