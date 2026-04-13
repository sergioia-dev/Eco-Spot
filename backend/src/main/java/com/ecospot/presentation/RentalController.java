package com.ecospot.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ecospot.business.dato.CreateRentalRequest;
import com.ecospot.business.dato.UpdateRentalRequest;
import com.ecospot.business.service.RentalService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/host")
public class RentalController {

  private final RentalService rentalService;

  public RentalController(RentalService rentalService) {
    this.rentalService = rentalService;
  }

  @PostMapping("/rentals")
  public ResponseEntity<Void> createRental(
      @RequestHeader("Authorization") String authorizationHeader,
      @ModelAttribute CreateRentalRequest request,
      @RequestParam(value = "images", required = false) List<MultipartFile> images) {

    if (request.getName() == null || request.getName().isEmpty() ||
        request.getContact() == null || request.getContact().isEmpty() ||
        request.getSize() == null || request.getPeopleQuantity() == null ||
        request.getRooms() == null || request.getBathrooms() == null ||
        request.getCity() == null || request.getCity().isEmpty() ||
        request.getCountry() == null || request.getCountry().isEmpty() ||
        request.getValueNight() == null) {
      return ResponseEntity.badRequest().build();
    }

    if (images != null && images.size() > 3) {
      return ResponseEntity.badRequest().build();
    }

    request.setImages(images);

    String token = authorizationHeader.replace("Bearer ", "");
    boolean created = rentalService.createRental(token, request);

    if (!created) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("/rentals/{rentalId}")
  public ResponseEntity<Void> updateRental(
      @RequestHeader("Authorization") String authorizationHeader,
      @PathVariable UUID rentalId,
      @ModelAttribute UpdateRentalRequest request,
      @RequestParam(value = "images", required = false) List<MultipartFile> images) {

    if (request.getName() == null || request.getName().isEmpty() ||
        request.getContact() == null || request.getContact().isEmpty() ||
        request.getSize() == null || request.getPeopleQuantity() == null ||
        request.getRooms() == null || request.getBathrooms() == null ||
        request.getCity() == null || request.getCity().isEmpty() ||
        request.getCountry() == null || request.getCountry().isEmpty() ||
        request.getValueNight() == null) {
      return ResponseEntity.badRequest().build();
    }

    if (images != null && images.size() > 3) {
      return ResponseEntity.badRequest().build();
    }

    request.setImages(images);

    String token = authorizationHeader.replace("Bearer ", "");
    boolean updated = rentalService.updateRental(token, rentalId, request);

    if (!updated) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/rentals/{rentalId}")
  public ResponseEntity<Void> deleteRental(
      @RequestHeader("Authorization") String authorizationHeader,
      @PathVariable UUID rentalId) {

    String token = authorizationHeader.replace("Bearer ", "");
    boolean deleted = rentalService.deleteRental(token, rentalId);

    if (!deleted) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return ResponseEntity.ok().build();
  }

}
