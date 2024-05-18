package com.iglusoft.api.database.entities;

import com.iglusoft.api.enums.DiscountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "offer")
    private List<OfferIngredientMinQuantity> requiredIngredients = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "offer")
    private List<OfferIngredientMinQuantity> excludedIngredients = new ArrayList<>();;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @Positive
    private Double discountAmount;

    public Offer() {
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

    public List<OfferIngredientMinQuantity> getRequiredIngredients() {
        return requiredIngredients;
    }

    public List<OfferIngredientMinQuantity> getExcludedIngredients() {
        return excludedIngredients;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }
}
