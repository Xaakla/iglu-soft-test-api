package com.iglusoft.api.enums;

import com.iglusoft.api.database.entities.Offer;
import com.iglusoft.api.dtos.DishIngredientDto;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public enum DiscountType {
    DISH_TOTAL_PRICE_PERCENTAGE_DISCOUNT(
        (totalNoDiscount, offer, ingredients) -> Math.max(0L, (totalNoDiscount * offer.getDiscountAmount() / 100))
    ),
    INGREDIENT_QUANTITY_DISCOUNT(
        (totalNoDiscount, offer, ingredients) -> {
            var discountTotal = new AtomicLong(0L);
            offer.getRequiredIngredients().forEach(offerIngredientMinQuantity -> {
                var ingredientId = offerIngredientMinQuantity.getIngredient().getId();
                var ingredientPrice = offerIngredientMinQuantity.getIngredient().getSalePrice();
                ingredients.stream().filter(it -> Objects.equals(it.ingredientId(), ingredientId)).forEach(ingredient -> {
                    var ingredientTotalNoDiscount = ingredient.quantity() * ingredientPrice;
                    var timesToApply = ingredient.quantity() / offerIngredientMinQuantity.getMinQuantity();
                    var ingredientsToPay = offerIngredientMinQuantity.getPaidQuantity() * timesToApply + (ingredient.quantity() % offerIngredientMinQuantity.getMinQuantity());
                    var ingredientTotalWithDiscount = ingredientsToPay * ingredientPrice;
                    var discountValue = ingredientTotalNoDiscount - ingredientTotalWithDiscount;
                    discountTotal.set(discountTotal.get() + discountValue);
                });
            });

            return discountTotal.get();
        }
    ),

    ;
    public final CalculateDiscountAmount calculateDiscountAmount;

    DiscountType(CalculateDiscountAmount calculateDiscountAmount) {
        this.calculateDiscountAmount = calculateDiscountAmount;
    }

    public interface CalculateDiscountAmount {
        Long apply(Long totalNoDiscount, Offer offer, List<DishIngredientDto> ingredients);
    }
}
