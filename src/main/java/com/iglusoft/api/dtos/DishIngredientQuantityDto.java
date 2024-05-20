package com.iglusoft.api.dtos;

import com.iglusoft.api.database.entities.DishIngredientQuantity;
import com.iglusoft.api.database.entities.Ingredient;

public class DishIngredientQuantityDto {
    private Long id;
    private int quantity;
    private IngredientDto ingredient;

    DishIngredientQuantityDto() {}

    DishIngredientQuantityDto(DishIngredientQuantity dishIngredientQuantity) {
        this.id = dishIngredientQuantity.getId();
        this.quantity = dishIngredientQuantity.getQuantity();
        this.ingredient = new IngredientDto(dishIngredientQuantity.getIngredient());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public IngredientDto getIngredient() {
        return ingredient;
    }

    public void setIngredient(IngredientDto ingredient) {
        this.ingredient = ingredient;
    }
}
