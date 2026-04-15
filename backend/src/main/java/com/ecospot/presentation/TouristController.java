package com.ecospot.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecospot.business.service.TouristService;
import com.ecospot.business.dato.CreateReservationRequest;
import com.ecospot.business.dato.ItemCategory;
import com.ecospot.business.dato.ItemsResponse;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tourist")
public class TouristController {

  private final TouristService touristService;

  public TouristController(TouristService touristService) {
    this.touristService = touristService;
  }

  @GetMapping("/items")
  public ResponseEntity<ItemsResponse> getItems(
      @RequestHeader("Authorization") String authorizationHeader) {

    String token = authorizationHeader.replace("Bearer ", "");

    ItemsResponse items = touristService.getItemsByLocation(token);

    if (items == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    return ResponseEntity.ok(items);
  }

  @GetMapping("/search")
  public ResponseEntity<Object> search(
      @RequestHeader("Authorization") String authorizationHeader,
      @RequestParam(value = "category", required = false) String category,
      @RequestParam("searchBy") String searchBy) {

    ItemCategory itemCategory = null;
    if (category != null && !category.isEmpty()) {
      try {
        itemCategory = ItemCategory.valueOf(category.toUpperCase());
      } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
      }
    }

    if (searchBy == null || searchBy.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }

    String token = authorizationHeader.replace("Bearer ", "");

    Object results = touristService.search(token, itemCategory, searchBy);

    if (results == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    return ResponseEntity.ok(results);
  }

  @PostMapping("/rentals/{rentalId}/reservations")
  public ResponseEntity<Void> createReservation(
      @RequestHeader("Authorization") String authorizationHeader,
      @PathVariable UUID rentalId,
      @RequestBody CreateReservationRequest request) {

    String token = authorizationHeader.replace("Bearer ", "");
    boolean created = touristService.createReservation(token, rentalId, request);

    if (!created) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
