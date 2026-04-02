package com.ecospot.presentation.DTO;

public class CreateUserRequest {

  private String name;
  private String surname;
  private String email;
  private String password;
  private String rol;

  public CreateUserRequest() {
  }

  public CreateUserRequest(String name, String surname, String email, String password, String rol) {
    this.name = name;
    this.surname = surname;
    this.email = email;
    this.password = password;
    this.rol = rol;
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

  public String getRol() {
    return rol;
  }

  public void setRol(String rol) {
    this.rol = rol;
  }

}
