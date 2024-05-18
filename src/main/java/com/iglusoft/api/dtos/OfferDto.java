package com.iglusoft.api.dtos;

import com.iglusoft.api.database.entities.Offer;
import com.iglusoft.api.enums.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public class OfferDto {
    @Positive
    private Long id;

    @NotBlank
    private String name;

    private List<OfferIngredientMinQuantityDto> requiredIngredients;

    private List<OfferIngredientMinQuantityDto> excludedIngredients;

    @NotNull
    private DiscountType discountType;

    @Positive
    private Long discountAmount;

    public OfferDto() {
    }

    public OfferDto(Offer offer) {
        this.id = offer.getId();
        this.name = offer.getName();
        this.discountType = offer.getDiscountType();
        this.discountAmount = offer.getDiscountAmount();
        this.requiredIngredients = offer.getRequiredIngredients().stream().map(OfferIngredientMinQuantityDto::new).toList();
        this.excludedIngredients = offer.getExcludedIngredients().stream().map(OfferIngredientMinQuantityDto::new).toList();
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

    public List<OfferIngredientMinQuantityDto> getRequiredIngredients() {
        return requiredIngredients;
    }

    public void setRequiredIngredients(List<OfferIngredientMinQuantityDto> requiredIngredients) {
        this.requiredIngredients = requiredIngredients;
    }

    public List<OfferIngredientMinQuantityDto> getExcludedIngredients() {
        return excludedIngredients;
    }

    public void setExcludedIngredients(List<OfferIngredientMinQuantityDto> excludedIngredients) {
        this.excludedIngredients = excludedIngredients;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public Long getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Long discountAmount) {
        this.discountAmount = discountAmount;
    }
}
