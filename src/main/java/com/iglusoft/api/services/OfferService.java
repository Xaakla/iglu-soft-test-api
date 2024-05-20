package com.iglusoft.api.services;

import com.iglusoft.api.database.entities.Offer;
import com.iglusoft.api.database.entities.OfferIngredientMinQuantity;
import com.iglusoft.api.database.repositories.IngredientRepository;
import com.iglusoft.api.database.repositories.OfferIngredientMinQuantityRepository;
import com.iglusoft.api.database.repositories.OfferRepository;
import com.iglusoft.api.dtos.DishOrderDto;
import com.iglusoft.api.dtos.NewEditOffer;
import com.iglusoft.api.dtos.NewEditOfferIngredientMinQuantityDto;
import com.iglusoft.api.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfferService {
    private final OfferRepository offerRepository;
    private final OfferIngredientMinQuantityRepository offerIngredientMinQuantityRepository;
    private final IngredientService ingredientService;

    public OfferService(
            OfferRepository offerRepository,
            OfferIngredientMinQuantityRepository offerIngredientMinQuantityRepository,
            IngredientService ingredientService) {
        this.offerRepository = offerRepository;
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

        populateOfferIngredientList(offerToSave, offerToSave.getRequiredIngredients(), newEditOffer.requiredIngredients());
        populateOfferIngredientList(offerToSave, offerToSave.getExcludedIngredients(), newEditOffer.excludedIngredients());

        return offerRepository.save(offerToSave);
    }

    public List<Offer> findAllOffers() {
        return offerRepository.findAll();
    }

    public Offer findOfferById(long id) {
        return offerRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public void deleteOffer(Long id) {
        if (!offerRepository.existsById(id))
            throw new NotFoundException();

        offerIngredientMinQuantityRepository.deleteAllByOfferId(id);
        offerRepository.deleteById(id);
    }

    private void populateOfferIngredientList(Offer offer, List<OfferIngredientMinQuantity> listToPopulate, List<NewEditOfferIngredientMinQuantityDto> data) {
        listToPopulate.clear();
        listToPopulate.addAll(data.stream().map(it -> getOfferIngredientMinQuantity(it, offer)).toList());
    }

    private OfferIngredientMinQuantity getOfferIngredientMinQuantity(NewEditOfferIngredientMinQuantityDto it, Offer offerToSave) {
        var requiredIngredient = ingredientService.findById(it.getIngredientId());
        var offerIngredientMinQuantity = new OfferIngredientMinQuantity();

        offerIngredientMinQuantity.setMinQuantity(it.getMinQuantity());
        offerIngredientMinQuantity.setPaidQuantity(it.getPaidQuantity());
        offerIngredientMinQuantity.setIngredient(requiredIngredient);
        offerIngredientMinQuantity.setOffer(offerToSave);

        return offerIngredientMinQuantity;
    }
}
