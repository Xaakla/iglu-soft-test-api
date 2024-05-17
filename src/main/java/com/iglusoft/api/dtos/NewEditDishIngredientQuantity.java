package com.iglusoft.api.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record NewEditDishIngredientQuantity(
        @Positive @NotNull int quantity,
        @Positive @NotNull Long ingredientId
) {
}
