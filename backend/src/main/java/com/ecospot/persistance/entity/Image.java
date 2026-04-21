package com.ecospot.persistance.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "images")
public class Image {

  @Id
  @Column(name = "id", nullable = false, columnDefinition = "UUID DEFAULT gen_random_uuid()")
  private UUID id = UUID.randomUUID();

  @Column(name = "extension", nullable = false, length = 10)
  private String extension = "";

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "rental_id", nullable = true)
  private Rental rental;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "business_id", nullable = true)
  private Business business;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "experience_id", nullable = true)
  private Experience experience;

  public Image() {
  }

  public Image(UUID id, String extension, Rental rental) {
    this.id = id;
    this.extension = extension;
    this.rental = rental;
  }

  public Image(UUID id, String extension, Business business) {
    this.id = id;
    this.extension = extension;
    this.business = business;
  }

  public Image(UUID id, String extension, Experience experience) {
    this.id = id;
    this.extension = extension;
    this.experience = experience;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getExtension() {
    return extension;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }

  public Rental getRental() {
    return rental;
  }

  public void setRental(Rental rental) {
    this.rental = rental;
  }

  public Business getBusiness() {
    return business;
  }

  public void setBusiness(Business business) {
    this.business = business;
  }

  public Experience getExperience() {
    return experience;
  }

  public void setExperience(Experience experience) {
    this.experience = experience;
  }

}