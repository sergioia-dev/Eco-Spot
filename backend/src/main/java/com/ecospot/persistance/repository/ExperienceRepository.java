
package com.ecospot.persistance.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecospot.persistance.entity.Experience;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, UUID> {

  List<Experience> findByCityAndCountry(String city, String country);

}
