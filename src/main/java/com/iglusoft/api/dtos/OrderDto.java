package com.iglusoft.api.dtos;

import java.util.List;

public record OrderDto(
        List<DishDto> dishes
) {
}
