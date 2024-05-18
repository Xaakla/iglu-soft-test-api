package com.iglusoft.api.dtos;

import com.iglusoft.api.enums.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record NewEditOffer(
        @Positive Long id,
        @NotBlank String name,
        List<NewEditOfferIngredientMinQuantityDto> requiredIngredients,
        List<NewEditOfferIngredientMinQuantityDto> excludedIngredients,
        DiscountType discountType,
        @Positive Double discountAmount
) {
}
