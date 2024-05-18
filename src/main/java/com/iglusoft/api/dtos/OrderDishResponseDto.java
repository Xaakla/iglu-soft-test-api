package com.iglusoft.api.dtos;

import java.util.List;

public record OrderDishResponseDto(
        String name,
        Long salePrice,
        List<OrderIngredientResponseDto> ingredients
) {
}
