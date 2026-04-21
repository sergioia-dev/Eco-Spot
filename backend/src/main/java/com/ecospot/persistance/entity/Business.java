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
@Table(name = "businesses")
public class Business {

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

  @Column(name = "city", nullable = false, length = 80)
  private String city = "";

  @Column(name = "country", nullable = false, length = 80)
  private String country = "";

  @Column(name = "location", nullable = true, columnDefinition = "TEXT")
  private String location = "";

  @Column(name = "menu", nullable = true, columnDefinition = "TEXT")
  private String menu = "";

  @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Image> images = new ArrayList<>();

  public Business() {
  }

  public Business(User user, String name, String description, String contact, String city,
      String country, String location, String menu) {
    this.user = user;
    this.name = name;
    this.description = description;
    this.contact = contact;
    this.city = city != null ? city.toUpperCase() : "";
    this.country = country != null ? country.toUpperCase() : "";
    this.location = location;
    this.menu = menu;
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

  public String getMenu() {
    return menu;
  }

  public void setMenu(String menu) {
    this.menu = menu;
  }

  public List<Image> getImages() {
    return images;
  }

  public void setImages(List<Image> images) {
    this.images = images;
  }

}