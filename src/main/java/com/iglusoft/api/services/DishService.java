package com.iglusoft.api.services;

import com.iglusoft.api.commons.ObjectValidationResponse;
import com.iglusoft.api.database.entities.Offer;
import com.iglusoft.api.database.repositories.OfferRepository;
import com.iglusoft.api.dtos.NewEditDishDto;
import com.iglusoft.api.database.entities.Dish;
import com.iglusoft.api.database.entities.DishIngredientQuantity;
import com.iglusoft.api.enums.DiscountType;
import com.iglusoft.api.exceptions.BusinessException;
import com.iglusoft.api.exceptions.NotFoundException;
import com.iglusoft.api.interfaces.IValidatesObject;
import com.iglusoft.api.database.repositories.DishRepository;
import com.iglusoft.api.database.repositories.IngredientRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class DishService implements IValidatesObject<Dish> {
    private final DishRepository dishRepository;
    private final IngredientRepository ingredientRepository;
    private final OfferRepository offerRepository;

    public DishService(
            DishRepository dishRepository,
            IngredientRepository ingredientRepository,
            OfferRepository offerRepository) {
        this.dishRepository = dishRepository;
        this.ingredientRepository = ingredientRepository;
        this.offerRepository = offerRepository;
    }

    @Transactional
    public Dish saveDish(NewEditDishDto newEditDishDto) {
        boolean isEdit = newEditDishDto.id() != null;
        var dishToSave = isEdit ?
                dishRepository.findById(newEditDishDto.id())
                        .orElseThrow(NotFoundException::new) : new Dish();

        dishToSave.setName(newEditDishDto.name());

        dishToSave.getIngredients().clear();
        dishToSave.getIngredients().addAll(newEditDishDto.ingredientsIds().stream().map(it -> {
            var ingredient = this.ingredientRepository.findById(it.ingredientId()).orElseThrow(NotFoundException::new);
            return new DishIngredientQuantity(dishToSave, ingredient, it.quantity());
        }).toList());

        dishToSave.setTotalPrice(calculateTotalPrice(dishToSave.getIngredients()));
        var response = this.validate(dishToSave);
        if (response.isInvalid())
            throw new BusinessException(response.message());

        findAllOffersByDish(dishToSave);

        return this.dishRepository.save(dishToSave);
    }

    public List<Dish> findAllDishes() {
        return this.dishRepository.findAll();
    }

    @Transactional
    public void deleteDish(Long id) {
        if (!this.dishRepository.existsById(id))
            throw new NotFoundException();

        this.dishRepository.deleteById(id);
    }

    private Double calculateTotalPrice(List<DishIngredientQuantity> dishIngredientQuantities) {
        AtomicReference<Double> totalPrice = new AtomicReference<>(0D);

        dishIngredientQuantities.forEach((DishIngredientQuantity dishIngredientQuantity) -> {
            double ingredientPrice = dishIngredientQuantity.getIngredient().getSalePrice();
            double quantity = dishIngredientQuantity.getQuantity();
            double itemTotalPrice = ingredientPrice * quantity;
            totalPrice.updateAndGet(v -> v + itemTotalPrice);
        });

        return totalPrice.get();
    }

    public List<Offer> findAllOffersByDish(Dish dish) {
        var offers = offerRepository.findAll();

        var validOffers = offers.stream().map(offer -> {
            var excludedIngredientsIds = offer.getExcludedIngredients().stream().map(it -> it.getIngredient().getId()).toList();
            var requiredIngredientsIds = offer.getRequiredIngredients().stream().map(it -> it.getIngredient().getId()).toList();

            if (dish.getIngredients().stream().noneMatch(it -> excludedIngredientsIds.contains(it.getId()))
                    && dish.getIngredients().containsAll(requiredIngredientsIds)) {
                System.out.println(dish);
            }
//            offer.getExcludedIngredients()

            return offer;
        }).toList();

        return validOffers;
    }

    public void recalculateDishesTotalPriceByOffer(Offer offer) {
        var excludedIngredientsIds = offer.getExcludedIngredients().stream().map(it -> it.getIngredient().getId()).toList();
        var requiredIngredientsIds = offer.getRequiredIngredients().stream().map(it -> it.getIngredient().getId()).toList();

        var offeredDishes = findAllDishes().stream()
                .filter(dish -> dish.getIngredients().stream().noneMatch(it -> excludedIngredientsIds.contains(it.getId())))
                .filter(dish -> dish.getIngredients().stream().anyMatch(it -> requiredIngredientsIds.contains(it.getId())))
                .toList();

        if (offer.getDiscountType() == DiscountType.DISH_TOTAL_PRICE_PERCENTAGE_DISCOUNT) {
            dishRepository.saveAll(
                offeredDishes.stream().peek(dish -> {
                    Double absolutePrice = calculateTotalPrice(dish.getIngredients());

                    dish.setTotalPrice(absolutePrice - ((offer.getDiscountAmount() / 100) * absolutePrice));
                }).toList());
        }
    }

    @Override
    public ObjectValidationResponse validate(Dish dish) {
        if (dish.getName().isEmpty()) {
            return new ObjectValidationResponse(false, "Field 'name' cannot be empty.");
        }
        if (dish.getTotalPrice() <= 0) {
            return new ObjectValidationResponse(false, "Field 'totalPrice' cannot be less or equal than zero.");
        }
        if (this.dishRepository.existsByNameAndIdNot(dish.getName(), dish.getId())) {
            return new ObjectValidationResponse(false, String.format("Dish with name '%s' already exists.", dish.getName()));
        }

        return new ObjectValidationResponse(true, "Validated successfully.");
    }
}
