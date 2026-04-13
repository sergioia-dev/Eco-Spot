package com.ecospot.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageStorage {
  private static final Logger logger = LoggerFactory.getLogger(ImageStorage.class);

  private static final List<String> ALLOWED_TYPES = Arrays.asList("image/jpeg", "image/png", "image/webp");
  private static final String UPLOAD_DIR = "images";

  public ImageStorage() {
    try {
      Path uploadPath = Paths.get(UPLOAD_DIR);
      if (!Files.exists(uploadPath)) {
        Files.createDirectories(uploadPath);
        logger.info("Created images directory: {}", uploadPath.toAbsolutePath());
      }
    } catch (IOException e) {
      logger.error("Failed to create images directory: {}", e.getMessage(), e);
    }
  }

  public static class SavedImage {
    private final UUID id;
    private final String extension;

    public SavedImage(UUID id, String extension) {
      this.id = id;
      this.extension = extension;
    }

    public UUID getId() {
      return id;
    }

    public String getExtension() {
      return extension;
    }

    public String getFilename() {
      return id.toString() + "." + extension;
    }
  }

  public SavedImage saveImage(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return null;
    }

    if (!ALLOWED_TYPES.contains(file.getContentType())) {
      logger.warn("Invalid file type: {}. Allowed types: jpg, png, webp", file.getContentType());
      return null;
    }

    try {
      String extension = getExtension(file.getOriginalFilename());
      UUID imageId = UUID.randomUUID();
      String filename = imageId.toString() + "." + extension;
      Path filePath = Paths.get(UPLOAD_DIR, filename);

      Files.copy(file.getInputStream(), filePath);
      logger.info("Saved image: {}", filename);

      return new SavedImage(imageId, extension);
    } catch (IOException e) {
      logger.error("Failed to save image: {}", e.getMessage(), e);
      return null;
    }
  }

  public boolean deleteImage(UUID id, String extension) {
    if (id == null || extension == null || extension.isEmpty()) {
      return false;
    }

    try {
      String filename = id.toString() + "." + extension;
      Path filePath = Paths.get(UPLOAD_DIR, filename);
      return Files.deleteIfExists(filePath);
    } catch (IOException e) {
      logger.error("Failed to delete image: {}", e.getMessage(), e);
      return false;
    }
  }

  private String getExtension(String filename) {
    if (filename == null) {
      return "";
    }
    int lastDot = filename.lastIndexOf('.');
    if (lastDot > 0) {
      return filename.substring(lastDot + 1).toLowerCase();
    }
    return "";
  }

  public String getImagePath(UUID id, String extension) {
    if (id == null || extension == null || extension.isEmpty()) {
      return "";
    }
    return UPLOAD_DIR + "/" + id.toString() + "." + extension;
  }
}