package com.ecospot.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecospot.business.TouristService;

@RestController
@RequestMapping("/api/v1/tourist")
public class TouristController {

  private final TouristService touristService;

  public TouristController(TouristService touristService) {
    this.touristService = touristService;
  }

  @GetMapping("/items")
  public ResponseEntity<Object> getItemsByCategory(
      @RequestHeader("Authorization") String authorizationHeader,
      @RequestParam("category") String category) {

    String token = authorizationHeader.replace("Bearer ", "");

    Object items = touristService.getItemsByCategory(token, category);

    if (items == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    return ResponseEntity.ok(items);
  }
}