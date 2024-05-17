package com.iglusoft.api.database.repositories;

import com.iglusoft.api.database.entities.DishIngredientQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DishIngredientQuantityRepository extends JpaRepository<DishIngredientQuantity, Long> {
    boolean existsByIngredientId(Long id);
}
