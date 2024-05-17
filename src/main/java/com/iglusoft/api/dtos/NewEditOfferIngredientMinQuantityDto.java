package com.iglusoft.api.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class NewEditOfferIngredientMinQuantityDto {
    @Positive
    private Long id;

    @Positive
    @NotNull
    private Long ingredientId;

    @Positive
    @NotNull
    private int minQuantity;

    @Positive
    private int paidQuantity;

    public NewEditOfferIngredientMinQuantityDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Long ingredientId) {
        this.ingredientId = ingredientId;
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
