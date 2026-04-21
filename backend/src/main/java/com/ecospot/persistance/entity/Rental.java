package com.ecospot.persistance.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "rentals")
public class Rental {

  @Id
  @Column(name = "id", nullable = false, columnDefinition = "UUID DEFAULT gen_random_uuid()")
  private UUID id = UUID.randomUUID();

  @Column(name = "created_at", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private LocalDateTime createdAt;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "is_enable", nullable = false)
  private boolean isEnable = true;

  @Column(name = "name", nullable = false, length = 100)
  private String name = "";

  @Column(name = "description", nullable = true, length = 300)
  private String description = "";

  @Column(name = "contact", nullable = false, length = 10)
  private String contact = "";

  @Column(name = "size", nullable = false)
  private int size = 0;

  @Column(name = "people_quantity", nullable = false)
  private int peopleQuantity = 0;

  @Column(name = "rooms", nullable = false)
  private int rooms = 0;

  @Column(name = "bathrooms", nullable = false)
  private int bathrooms = 0;

  @Column(name = "city", nullable = false, length = 80)
  private String city = "";

  @Column(name = "country", nullable = false, length = 80)
  private String country = "";

  @Column(name = "location", nullable = true, columnDefinition = "TEXT")
  private String location = "";

  @Column(name = "value_night", nullable = false)
  private double valueNight = 0.0;

  @OneToMany(mappedBy = "rental", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Image> images = new ArrayList<>();

  @OneToMany(mappedBy = "rental", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Review> reviews = new ArrayList<>();

  public Rental() {
  }

  public Rental(User user, String name, String description, String contact, int size,
      int peopleQuantity, int rooms, int bathrooms, String city, String country,
      String location, double valueNight) {
    this.user = user;
    this.name = name;
    this.description = description;
    this.contact = contact;
    this.size = size;
    this.peopleQuantity = peopleQuantity;
    this.rooms = rooms;
    this.bathrooms = bathrooms;
    this.city = city != null ? city.toUpperCase() : "";
    this.country = country != null ? country.toUpperCase() : "";
    this.location = location;
    this.valueNight = valueNight;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public boolean isEnable() {
    return isEnable;
  }

  public void setEnable(boolean isEnable) {
    this.isEnable = isEnable;
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
    this.city = city != null ? city.toUpperCase() : "";
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country != null ? country.toUpperCase() : "";
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

  public List<Image> getImages() {
    return images;
  }

  public void setImages(List<Image> images) {
    this.images = images;
  }

  public List<Review> getReviews() {
    return reviews;
  }

  public void setReviews(List<Review> reviews) {
    this.reviews = reviews;
  }

}
