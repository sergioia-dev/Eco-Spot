package com.ecospot.business.dato;

import java.time.LocalDate;
import java.util.UUID;

public class ReservationResponse {

  private UUID id;
  private UUID rentalId;
  private String rentalName;
  private String userName;
  private String userSurname;
  private LocalDate startingDate;
  private LocalDate endDate;
  private boolean isCancelled;

  public ReservationResponse() {
  }

  public ReservationResponse(UUID id, UUID rentalId, String rentalName, String userName,
      String userSurname, LocalDate startingDate, LocalDate endDate, boolean isCancelled) {
    this.id = id;
    this.rentalId = rentalId;
    this.rentalName = rentalName;
    this.userName = userName;
    this.userSurname = userSurname;
    this.startingDate = startingDate;
    this.endDate = endDate;
    this.isCancelled = isCancelled;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getRentalId() {
    return rentalId;
  }

  public void setRentalId(UUID rentalId) {
    this.rentalId = rentalId;
  }

  public String getRentalName() {
    return rentalName;
  }

  public void setRentalName(String rentalName) {
    this.rentalName = rentalName;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserSurname() {
    return userSurname;
  }

  public void setUserSurname(String userSurname) {
    this.userSurname = userSurname;
  }

  public LocalDate getStartingDate() {
    return startingDate;
  }

  public void setStartingDate(LocalDate startingDate) {
    this.startingDate = startingDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public boolean isCancelled() {
    return isCancelled;
  }

  public void setCancelled(boolean cancelled) {
    isCancelled = cancelled;
  }
}