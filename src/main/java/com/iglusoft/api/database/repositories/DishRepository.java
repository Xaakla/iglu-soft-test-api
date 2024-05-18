package com.iglusoft.api.database.repositories;

import com.iglusoft.api.database.entities.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
    boolean existsByNameAndIdNot(String name, Long id);
}
