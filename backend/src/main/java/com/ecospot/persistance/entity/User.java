package com.ecospot.persistance.entity;

import java.util.UUID;
import java.time.LocalDateTime;

import com.ecospot.persistance.dato.Roles;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

  @Id
  @Column(name = "id", nullable = false, columnDefinition = "UUID DEFAULT gen_random_uuid()")
  private UUID id = UUID.randomUUID();

  @Column(name = "name", nullable = false, length = 80)
  private String name = "";

  @Column(name = "surname", nullable = false, length = 100)
  private String surname = "";

  @Column(name = "email", nullable = false, unique = true, length = 320)
  private String email;

  @JsonIgnore
  @Column(name = "password", nullable = false, columnDefinition = "TEXT")
  private String password = "";

  @Column(name = "city", nullable = true, length = 80)
  private String currentCity = null;

  @Column(name = "country", nullable = true, length = 80)
  private String currentCountry = null;

  @Enumerated(EnumType.STRING)
  private Roles rol;

  @Column(name = "created_at", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private LocalDateTime created_at;

  public User() {
  }

  public User(String name, String surname, String email, String password, String current_city,
      String current_country, Roles rol) {
    this.name = name;
    this.surname = surname;
    this.email = email;
    this.password = password;
    this.currentCity = current_city;
    this.currentCountry = current_country;
    this.rol = rol;
  }

  public User(String name, String surname, String email, String password, Roles rol) {
    this.name = name;
    this.surname = surname;
    this.email = email;
    this.password = password;
    this.rol = rol;
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

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getCurrentCity() {
    return currentCity;
  }

  public void setCurrentCity(String current_city) {
    this.currentCity = current_city;
  }

  public String getCurrentCountry() {
    return currentCountry;
  }

  public void setCurrentCountry(String current_country) {
    this.currentCountry = current_country;
  }

  public Roles getRol() {
    return rol;
  }

  public void setRol(Roles rol) {
    this.rol = rol;
  }

  @Override
  public String toString() {
    return "User [id=" + id + ", name=" + name + ", surname=" + surname + ", email=" + email + ", password=" + password
        + ", current_city=" + currentCity + ", current_country=" + currentCountry + ", rol=" + rol + "]";
  }

}
