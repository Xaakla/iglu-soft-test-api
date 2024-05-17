package com.iglusoft.api.services;

import com.iglusoft.api.database.entities.Offer;
import com.iglusoft.api.database.entities.OfferIngredientMinQuantity;
import com.iglusoft.api.database.repositories.IngredientRepository;
import com.iglusoft.api.database.repositories.OfferIngredientMinQuantityRepository;
import com.iglusoft.api.database.repositories.OfferRepository;
import com.iglusoft.api.dtos.NewEditOffer;
import com.iglusoft.api.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfferService {
    private final OfferRepository offerRepository;
    private final IngredientRepository ingredientRepository;
    private final OfferIngredientMinQuantityRepository offerIngredientMinQuantityRepository;
    private final IngredientService ingredientService;

    public OfferService(
            OfferRepository offerRepository,
            IngredientRepository ingredientRepository,
            OfferIngredientMinQuantityRepository offerIngredientMinQuantityRepository,
            IngredientService ingredientService) {
        this.offerRepository = offerRepository;
        this.ingredientRepository = ingredientRepository;
        this.offerIngredientMinQuantityRepository = offerIngredientMinQuantityRepository;
        this.ingredientService = ingredientService;
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


        if (isEdit)
            offerIngredientMinQuantityRepository.deleteAllByOfferId(offerToSave.getId());

        offerToSave.getRequiredIngredients().addAll(
                newEditOffer.requiredIngredients().stream().map(it -> {
                    var requiredIngredient = ingredientService.findById(it.getIngredientId());
                    var offerIngredientMinQuantity = new OfferIngredientMinQuantity();

                    offerIngredientMinQuantity.setMinQuantity(it.getMinQuantity());
                    offerIngredientMinQuantity.setPaidQuantity(it.getPaidQuantity());
                    offerIngredientMinQuantity.setIngredient(requiredIngredient);
                    offerIngredientMinQuantity.setOffer(offerToSave);

                    return offerIngredientMinQuantity;
                }).toList()
        );

        offerToSave.getExcludedIngredients().addAll(
                newEditOffer.excludedIngredients().stream().map(it -> {
                    var excludedIngredient = this.ingredientRepository.findById(it.getIngredientId()).orElseThrow(NotFoundException::new);
                    var offerIngredientMinQuantity = new OfferIngredientMinQuantity();

                    offerIngredientMinQuantity.setMinQuantity(it.getMinQuantity());
                    offerIngredientMinQuantity.setPaidQuantity(it.getPaidQuantity());
                    offerIngredientMinQuantity.setIngredient(excludedIngredient);
                    offerIngredientMinQuantity.setOffer(offerToSave);

                    return offerIngredientMinQuantity;
                }).toList()
        );

        return offerRepository.save(offerToSave);
    }

    public List<Offer> findAllOffers() {
        return offerRepository.findAll();
    }

    @Transactional
    public void deleteOffer(Long id) {
        if (!offerRepository.existsById(id))
            throw new NotFoundException();

        offerIngredientMinQuantityRepository.deleteAllByOfferId(id);
        offerRepository.deleteById(id);
    }
}
