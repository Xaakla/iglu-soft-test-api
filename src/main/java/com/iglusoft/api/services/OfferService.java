package com.iglusoft.api.services;

import com.iglusoft.api.database.entities.Offer;
import com.iglusoft.api.database.entities.OfferIngredientMinQuantity;
import com.iglusoft.api.database.repositories.IngredientRepository;
import com.iglusoft.api.database.repositories.OfferRepository;
import com.iglusoft.api.dtos.NewEditOffer;
import com.iglusoft.api.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class OfferService {
    private final OfferRepository offerRepository;
    private final IngredientRepository ingredientRepository;

    public OfferService(OfferRepository offerRepository, IngredientRepository ingredientRepository) {
        this.offerRepository = offerRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @Transactional
    public Offer saveOffer(NewEditOffer newEditOffer) {
        boolean isEdit = newEditOffer.id() != null;

        var offerToSave = isEdit ?
                offerRepository.findById(newEditOffer.id())
                        .orElseThrow(NotFoundException::new) : new Offer();

        offerToSave.setName(newEditOffer.name());
        offerToSave.setDiscountAmount(newEditOffer.discountAmount());
        offerToSave.setDiscountType(newEditOffer.discountType());

        offerToSave.getRequiredIngredients().clear();
        offerToSave.getExcludedIngredients().clear();
        offerToSave.getRequiredIngredients().addAll(
            newEditOffer.requiredIngredients().stream().map(it -> {
                var requiredIngredient = this.ingredientRepository.findById(it.getIngredientId()).orElseThrow(NotFoundException::new);
                return new OfferIngredientMinQuantity(
                        it.getId(),
                        requiredIngredient,
                        it.getMinQuantity(),
                        it.getPaidQuantity());
            }).toList()
        );
        offerToSave.getExcludedIngredients().addAll(
                newEditOffer.excludedIngredients().stream().map(it -> {
                    var excludedIngredient = this.ingredientRepository.findById(it.getIngredientId()).orElseThrow(NotFoundException::new);
                    return new OfferIngredientMinQuantity(
                            it.getId(),
                            excludedIngredient,
                            it.getMinQuantity(),
                            it.getPaidQuantity());
                }).toList()
        );

        return offerRepository.save(offerToSave);
    }
}
