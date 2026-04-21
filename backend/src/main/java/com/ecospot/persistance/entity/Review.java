package com.ecospot.persistance.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "reviews")
public class Review {

  @Id
  @Column(name = "id", nullable = false, columnDefinition = "UUID DEFAULT gen_random_uuid()")
  private UUID id = UUID.randomUUID();

  @Column(name = "created_at", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private LocalDateTime createdAt;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "rental_id", nullable = true)
  private Rental rental;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "business_id", nullable = true)
  private Business business;

  @Column(name = "qualification", nullable = false)
  private int qualification = 0;

  @Column(name = "opinion", nullable = true, length = 500)
  private String opinion = "";

  public Review() {
  }

  public Review(User user, Rental rental, Integer qualification, String opinion) {
    this.user = user;
    this.rental = rental;
    this.qualification = qualification;
    this.opinion = opinion;
  }

  public Review(User user, Business business, Integer qualification, String opinion) {
    this.user = user;
    this.business = business;
    this.qualification = qualification;
    this.opinion = opinion;
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

  public Integer getQualification() {
    return qualification;
  }

  public void setQualification(Integer qualification) {
    this.qualification = qualification;
  }

  public String getOpinion() {
    return opinion;
  }

  public void setOpinion(String opinion) {
    this.opinion = opinion;
  }

}
