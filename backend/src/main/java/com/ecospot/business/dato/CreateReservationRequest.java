package com.ecospot.business.dato;

import java.time.LocalDate;

public class CreateReservationRequest {

  private LocalDate startingDate;
  private LocalDate endDate;

  public CreateReservationRequest() {
  }

  public CreateReservationRequest(LocalDate startingDate, LocalDate endDate) {
    this.startingDate = startingDate;
    this.endDate = endDate;
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
}