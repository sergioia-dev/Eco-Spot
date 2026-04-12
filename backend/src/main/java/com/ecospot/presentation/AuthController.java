package com.ecospot.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecospot.business.service.AuthService;
import com.ecospot.presentation.dato.CreateUserRequest;
import com.ecospot.presentation.dato.LoginRequest;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public ResponseEntity<Void> createUser(@RequestBody CreateUserRequest request) {

    boolean created = authService.createUser(
        request.getName(),
        request.getSurname(),
        request.getEmail(),
        request.getPassword(),
        request.getCity(),
        request.getCountry(),
        request.getRol());

    if (created) {
      return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    return ResponseEntity.status(HttpStatus.CONFLICT).build();
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LoginRequest request) {
    String token = authService.login(request.getEmail(), request.getPassword());
    if (token != null) {
      return ResponseEntity.ok(token);
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

}
