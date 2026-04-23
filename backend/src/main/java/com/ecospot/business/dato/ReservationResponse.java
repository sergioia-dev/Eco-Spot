package com.ecospot.business.dato;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ReservationResponse {

  private UUID id;
  private String name;
  private String description;
  private String contact;
  private int size;
  private int peopleQuantity;
  private int rooms;
  private int bathrooms;
  private String city;
  private String country;
  private String location;
  private double valueNight;
  private boolean isEnable;
  private double reviewAverage;
  private List<RentalResponse.ImageInfo> images;

  private LocalDate startingDate;
  private LocalDate endDate;
  private double price;
  private boolean isCancelled;

  public ReservationResponse() {
  }

  public ReservationResponse(UUID id, UUID rentalId, String rentalName, String userName,
      String userSurname, LocalDate startingDate, LocalDate endDate, boolean isCancelled) {
    this.id = id;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int getPeopleQuantity() {
    return peopleQuantity;
  }

  public void setPeopleQuantity(int peopleQuantity) {
    this.peopleQuantity = peopleQuantity;
  }

  public int getRooms() {
    return rooms;
  }

  public void setRooms(int rooms) {
    this.rooms = rooms;
  }

  public int getBathrooms() {
    return bathrooms;
  }

  public void setBathrooms(int bathrooms) {
    this.bathrooms = bathrooms;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public double getValueNight() {
    return valueNight;
  }

  public void setValueNight(double valueNight) {
    this.valueNight = valueNight;
  }

  public boolean isEnable() {
    return isEnable;
  }

  public void setEnable(boolean isEnable) {
    this.isEnable = isEnable;
  }

  public double getReviewAverage() {
    return reviewAverage;
  }

  public void setReviewAverage(double reviewAverage) {
    this.reviewAverage = reviewAverage;
  }

  public List<RentalResponse.ImageInfo> getImages() {
    return images;
  }

  public void setImages(List<RentalResponse.ImageInfo> images) {
    this.images = images;
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

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public boolean isCancelled() {
    return isCancelled;
  }

  public void setCancelled(boolean cancelled) {
    isCancelled = cancelled;
  }
}