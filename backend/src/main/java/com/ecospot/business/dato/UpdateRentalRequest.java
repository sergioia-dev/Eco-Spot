package com.ecospot.business.dato;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class UpdateRentalRequest {

  private String name;
  private String description;
  private String contact;
  private Integer size;
  private Integer peopleQuantity;
  private Integer rooms;
  private Integer bathrooms;
  private String city;
  private String country;
  private String location;
  private Double valueNight;
  private List<MultipartFile> images;

  public UpdateRentalRequest() {
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

  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  public Integer getPeopleQuantity() {
    return peopleQuantity;
  }

  public void setPeopleQuantity(Integer peopleQuantity) {
    this.peopleQuantity = peopleQuantity;
  }

  public Integer getRooms() {
    return rooms;
  }

  public void setRooms(Integer rooms) {
    this.rooms = rooms;
  }

  public Integer getBathrooms() {
    return bathrooms;
  }

  public void setBathrooms(Integer bathrooms) {
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

  public Double getValueNight() {
    return valueNight;
  }

  public void setValueNight(Double valueNight) {
    this.valueNight = valueNight;
  }

  public List<MultipartFile> getImages() {
    return images;
  }

  public void setImages(List<MultipartFile> images) {
    this.images = images;
  }

}