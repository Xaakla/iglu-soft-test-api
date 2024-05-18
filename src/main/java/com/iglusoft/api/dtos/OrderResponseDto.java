package com.iglusoft.api.dtos;

import java.util.List;

public record OrderResponseDto(
        Long totalPrice,
        List<OrderDishResponseDto> dishes
) {
}
