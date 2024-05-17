package com.iglusoft.api.dtos;

import com.iglusoft.api.database.entities.Ingredient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class IngredientDto {
    @Positive
    private Long id;

    @NotBlank
    private String name;

    @Positive
    @NotNull
    private Long salePrice;

    public IngredientDto() {}

    public IngredientDto(Ingredient ingredient) {
        this.id = ingredient.getId();
        this.name = ingredient.getName();
        this.salePrice = ingredient.getSalePrice();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Long salePrice) {
        this.salePrice = salePrice;
    }
}
