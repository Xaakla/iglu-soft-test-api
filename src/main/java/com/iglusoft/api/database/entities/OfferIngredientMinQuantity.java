package com.iglusoft.api.database.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class OfferIngredientMinQuantity {
    @Id
    private Long id;
    @OneToOne
    private Ingredient ingredient;
    private int minQuantity;
    private int paidQuantity;
}
