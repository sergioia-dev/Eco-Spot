package com.ecospot.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecospot.business.service.TouristService;
import com.ecospot.business.dato.ItemCategory;
import com.ecospot.presentation.dato.UpdateLocationRequest;

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

    if (category == null || category.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }

    ItemCategory itemCategory;
    try {
      itemCategory = ItemCategory.valueOf(category.toUpperCase());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }

    String token = authorizationHeader.replace("Bearer ", "");

    Object items = touristService.getItemsByCategory(token, itemCategory);

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

  @PatchMapping("/location")
  public ResponseEntity<Void> updateLocation(
      @RequestHeader("Authorization") String authorizationHeader,
      @RequestBody UpdateLocationRequest request) {

    if (request.getCity() == null || request.getCity().isEmpty() ||
        request.getCountry() == null || request.getCountry().isEmpty()) {
      return ResponseEntity.badRequest().build();
    }

    String token = authorizationHeader.replace("Bearer ", "");
    boolean updated = touristService.updateLocation(token, request.getCity(), request.getCountry());

    if (!updated) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    return ResponseEntity.ok().build();
  }
}
