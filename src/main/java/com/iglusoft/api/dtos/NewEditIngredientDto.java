package com.iglusoft.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record NewEditIngredientDto(
        @Positive Long id,
        @NotBlank String name,
        @Positive @NotNull Long salePrice
) {
}
