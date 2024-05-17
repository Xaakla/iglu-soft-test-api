package com.iglusoft.api.database.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
public class OfferIngredientMinQuantity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Ingredient ingredient;

    @Positive
    @NotNull
    private int minQuantity;

    @Positive
    private int paidQuantity;

    @ManyToOne
    private Offer offer;

    public OfferIngredientMinQuantity() {
    }

    public OfferIngredientMinQuantity(Long id, Ingredient ingredient, int minQuantity, int paidQuantity) {
        this.id = id;
        this.ingredient = ingredient;
        this.minQuantity = minQuantity;
        this.paidQuantity = paidQuantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
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

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }
}
