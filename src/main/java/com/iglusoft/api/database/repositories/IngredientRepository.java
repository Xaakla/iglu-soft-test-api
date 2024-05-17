package com.iglusoft.api.database.repositories;

import com.iglusoft.api.database.entities.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    boolean existsByNameAndIdNot(String name, Long id);
}
