package edu.gatech.ihi.nhaa.repository;

import edu.gatech.ihi.nhaa.entity.Nutrient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutrientRepository extends JpaRepository<Nutrient, Integer> {
}