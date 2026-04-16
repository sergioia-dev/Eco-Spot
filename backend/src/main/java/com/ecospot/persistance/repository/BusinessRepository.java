
package com.ecospot.persistance.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecospot.persistance.entity.Business;

@Repository
public interface BusinessRepository extends JpaRepository<Business, UUID> {

  List<Business> findByCityAndCountry(String city, String country);

  List<Business> findByNameContainingIgnoreCase(String name);

  List<Business> findByUserId(UUID userId);

}
