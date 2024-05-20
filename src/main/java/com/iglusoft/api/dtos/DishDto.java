package com.iglusoft.api.dtos;

import com.iglusoft.api.database.entities.Dish;
import com.iglusoft.api.database.entities.DishIngredientQuantity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class DishDto {
    @Positive
    private Long id;

    @NotBlank
    private String name;

    @Positive
    @NotNull
    private Long totalPrice;

    private List<DishIngredientQuantityDto> ingredients;

    public DishDto() {}

    public DishDto(Dish dish) {
        this.id = dish.getId();
        this.name = dish.getName();
        this.totalPrice = dish.getTotalPrice();
        this.ingredients = dish.getIngredients().stream().map(DishIngredientQuantityDto::new).toList();
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

    public Long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<DishIngredientQuantityDto> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<DishIngredientQuantityDto> ingredients) {
        this.ingredients = ingredients;
    }
}
