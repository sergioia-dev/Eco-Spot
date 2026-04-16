package com.ecospot.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ecospot.business.dato.BusinessResponse;
import com.ecospot.business.dato.CreateBusinessRequest;
import com.ecospot.business.service.BusinessService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class BusinessController {

  private final BusinessService businessService;

  public BusinessController(BusinessService businessService) {
    this.businessService = businessService;
  }

  @GetMapping("/businesses")
  public ResponseEntity<List<BusinessResponse>> getBusinesses(
      @RequestHeader("Authorization") String authorizationHeader,
      @RequestParam(value = "includeDisabled", required = false) boolean includeDisabled) {

    String token = authorizationHeader.replace("Bearer ", "");
    List<BusinessResponse> businesses = businessService.getBusinessesByToken(token, includeDisabled);

    return ResponseEntity.ok(businesses);
  }

  @PostMapping("/businesses")
  public ResponseEntity<Void> createBusiness(
      @RequestHeader("Authorization") String authorizationHeader,
      @ModelAttribute CreateBusinessRequest request,
      @RequestParam(value = "images", required = false) List<MultipartFile> images) {

    if (request.getName() == null || request.getName().isEmpty() ||
        request.getContact() == null || request.getContact().isEmpty() ||
        request.getCity() == null || request.getCity().isEmpty() ||
        request.getCountry() == null || request.getCountry().isEmpty()) {
      return ResponseEntity.badRequest().build();
    }

    if (images != null && images.size() > 3) {
      return ResponseEntity.badRequest().build();
    }

    request.setImages(images);

    String token = authorizationHeader.replace("Bearer ", "");
    boolean created = businessService.createBusiness(token, request);

    if (!created) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("/businesses/{businessId}")
  public ResponseEntity<Void> updateBusiness(
      @RequestHeader("Authorization") String authorizationHeader,
      @PathVariable UUID businessId,
      @ModelAttribute CreateBusinessRequest request,
      @RequestParam(value = "images", required = false) List<MultipartFile> images) {

    if (request.getName() == null || request.getName().isEmpty() ||
        request.getContact() == null || request.getContact().isEmpty() ||
        request.getCity() == null || request.getCity().isEmpty() ||
        request.getCountry() == null || request.getCountry().isEmpty()) {
      return ResponseEntity.badRequest().build();
    }

    if (images != null && images.size() > 3) {
      return ResponseEntity.badRequest().build();
    }

    request.setImages(images);

    String token = authorizationHeader.replace("Bearer ", "");
    boolean updated = businessService.updateBusiness(token, businessId, request);

    if (!updated) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/businesses/{businessId}")
  public ResponseEntity<Void> deleteBusiness(
      @RequestHeader("Authorization") String authorizationHeader,
      @PathVariable UUID businessId) {

    String token = authorizationHeader.replace("Bearer ", "");
    boolean deleted = businessService.deleteBusiness(token, businessId);

    if (!deleted) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return ResponseEntity.ok().build();
  }

  @PatchMapping("/businesses/{businessId}")
  public ResponseEntity<Void> setBusinessEnabled(
      @RequestHeader("Authorization") String authorizationHeader,
      @PathVariable UUID businessId,
      @RequestParam(value = "enabled") boolean enabled) {

    String token = authorizationHeader.replace("Bearer ", "");
    boolean updated = businessService.setBusinessEnabled(token, businessId, enabled);

    if (!updated) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return ResponseEntity.ok().build();
  }

}