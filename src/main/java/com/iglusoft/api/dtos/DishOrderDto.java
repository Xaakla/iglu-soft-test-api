package com.iglusoft.api.dtos;

import java.util.List;

public record DishOrderDto(
        Long dishId,
        List<DishIngredientDto> ingredients
) {
}
