package com.iglusoft.api.database.entities;

import com.iglusoft.api.enums.DiscountType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Offer {
    @Id
    private Long id;
    private String name;
    @OneToMany
    private List<OfferIngredientMinQuantity> requiredIngredients;
    @OneToMany
    private List<OfferIngredientMinQuantity> excludedIngredients;
    private DiscountType discountType;
    private Long discountAmount;
}
