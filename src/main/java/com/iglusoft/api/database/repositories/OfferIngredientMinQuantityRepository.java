package com.iglusoft.api.database.repositories;

import com.iglusoft.api.database.entities.OfferIngredientMinQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferIngredientMinQuantityRepository extends JpaRepository<OfferIngredientMinQuantity, Long> {
    Optional<List<OfferIngredientMinQuantity>> findAllByOfferId(Long offerId);

    void deleteAllByOfferId(Long offerId);
}
