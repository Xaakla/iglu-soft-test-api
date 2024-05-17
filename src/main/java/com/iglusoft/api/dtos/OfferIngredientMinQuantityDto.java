package com.iglusoft.api.dtos;

import com.iglusoft.api.database.entities.OfferIngredientMinQuantity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class OfferIngredientMinQuantityDto {
    @Positive
    private Long id;

    private IngredientDto ingredient;

    @Positive
    @NotNull
    private int minQuantity;

    @Positive
    private int paidQuantity;

    public OfferIngredientMinQuantityDto() {
    }

    public OfferIngredientMinQuantityDto(OfferIngredientMinQuantity offerIngredientMinQuantity) {
        this.id = offerIngredientMinQuantity.getId();
        this.ingredient = new IngredientDto(offerIngredientMinQuantity.getIngredient());
        this.minQuantity = offerIngredientMinQuantity.getMinQuantity();
        this.paidQuantity = offerIngredientMinQuantity.getPaidQuantity();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IngredientDto getIngredient() {
        return ingredient;
    }

    public void setIngredient(IngredientDto ingredient) {
        this.ingredient = ingredient;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        this.minQuantity = minQuantity;
    }

    public int getPaidQuantity() {
        return paidQuantity;
    }

    public void setPaidQuantity(int paidQuantity) {
        this.paidQuantity = paidQuantity;
    }
}
