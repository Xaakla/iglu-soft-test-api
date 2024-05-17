package com.iglusoft.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record NewEditDishDto(
        @Positive Long id,
        @NotBlank String name,
        List<NewEditDishIngredientQuantity> ingredientsIds
) {
}
