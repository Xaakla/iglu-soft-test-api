package com.iglusoft.api.database.repositories;

import com.iglusoft.api.database.entities.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
    boolean existsByNameAndIdNot(String name, Long id);
}
