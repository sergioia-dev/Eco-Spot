package com.ecospot.business.dato;

import java.util.List;
import java.util.UUID;

public class BusinessResponse {

  private UUID id;
  private String name;
  private String description;
  private String contact;
  private String city;
  private String country;
  private String location;
  private String menu;
  private boolean isEnable;
  private List<ImageInfo> images;

  public BusinessResponse() {
  }

  public BusinessResponse(UUID id, String name, String description, String contact,
      String city, String country, String location, String menu, boolean isEnable, List<ImageInfo> images) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.contact = contact;
    this.city = city;
    this.country = country;
    this.location = location;
    this.menu = menu;
    this.isEnable = isEnable;
    this.images = images;
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

  public String getMenu() {
    return menu;
  }

  public void setMenu(String menu) {
    this.menu = menu;
  }

  public boolean isEnable() {
    return isEnable;
  }

  public void setEnable(boolean enable) {
    isEnable = enable;
  }

  public List<ImageInfo> getImages() {
    return images;
  }

  public void setImages(List<ImageInfo> images) {
    this.images = images;
  }

  public static class ImageInfo {
    private UUID id;
    private String extension;

    public ImageInfo() {
    }

    public ImageInfo(UUID id, String extension) {
      this.id = id;
      this.extension = extension;
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
  }

}