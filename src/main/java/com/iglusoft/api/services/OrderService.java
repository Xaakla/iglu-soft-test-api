package com.iglusoft.api.services;

import com.iglusoft.api.database.entities.Offer;
import com.iglusoft.api.database.entities.OfferIngredientMinQuantity;
import com.iglusoft.api.dtos.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderService {

    private final DishService dishService;
    private final OfferService offerService;
    private final IngredientService ingredientService;

    OrderService(DishService dishService, OfferService offerService, IngredientService ingredientService) {

        this.dishService = dishService;
        this.offerService = offerService;
        this.ingredientService = ingredientService;
    }

    public OrderResponseDto getOrderResponse(List<DishOrderDto> orders) {
        var dishIdToTotalPriceMap = new HashMap<Long, OrderDishResponseDto>();
        for (DishOrderDto order : orders) {
            dishIdToTotalPriceMap.put(order.dishId(), calculateDishOrderFinalPrice(order));
        }
        var totalPrice = dishIdToTotalPriceMap.values().stream().mapToLong(OrderDishResponseDto::salePrice).sum();
        return new OrderResponseDto(totalPrice, new ArrayList<>(dishIdToTotalPriceMap.values()));
    }

    private OrderDishResponseDto calculateDishOrderFinalPrice(DishOrderDto order) {
        var dish = dishService.findById(order.dishId());
        var originalDishIngredients = dish.getIngredients().stream().map(it -> new DishIngredientDto(it.getIngredient().getId(), it.getQuantity())).toList();

        // Lista final de ingredientes usados
        var ingredientQuantities = combineIngredientQuantities(originalDishIngredients, order.ingredients());
        final var ingredientsTotalNoDiscount = calculateAllIngredientsTotal(ingredientQuantities);
        var dishTotalPrice = new AtomicLong(ingredientsTotalNoDiscount);

        var validOffers = getValidOffersForIngredientsList(ingredientQuantities);

        validOffers.forEach(offer -> {
            var discountAmount = offer.getDiscountType().calculateDiscountAmount.apply(ingredientsTotalNoDiscount, offer, ingredientQuantities);
            dishTotalPrice.set(Math.max(dishTotalPrice.get() - discountAmount, 0L));
        });

        var ingredientDetails = ingredientQuantities.stream().map(it -> {
            var ingredient = ingredientService.findById(it.ingredientId());
            return new OrderIngredientResponseDto(ingredient.getName(), it.quantity());
        }).toList();
        return new OrderDishResponseDto(dish.getName(), dishTotalPrice.get(), ingredientDetails);
    }

    private List<Offer> getValidOffersForIngredientsList(List<DishIngredientDto> ingredientQuantities) {
        return offerService.findAllOffers().stream()
                .filter(offer -> !ingredientListContainsAnyOfferExcludedIngredient(offer, ingredientQuantities))
                .filter(offer -> ingredientListContainsAllOfferRequiredIngredients(offer, ingredientQuantities))
                .toList();
    }

    private boolean ingredientListContainsAnyOfferExcludedIngredient(Offer offer, List<DishIngredientDto> ingredientQuantities) {
        return offer.getExcludedIngredients().stream().anyMatch(excluded -> dishOrderContainsIngredient(ingredientQuantities, excluded));
    }

    private boolean ingredientListContainsAllOfferRequiredIngredients(Offer offer, List<DishIngredientDto> ingredientQuantities) {
        return offer.getRequiredIngredients().stream().allMatch(required -> dishOrderContainsIngredient(ingredientQuantities, required));
    }

    private boolean dishOrderContainsIngredient(List<DishIngredientDto> ingredientQuantities, OfferIngredientMinQuantity offerIngredientMinQuantity) {
        return ingredientQuantities.stream().anyMatch(ingredient ->
                Objects.equals(ingredient.ingredientId(), offerIngredientMinQuantity.getIngredient().getId()) &&
                ingredient.quantity() >= offerIngredientMinQuantity.getMinQuantity()
        );
    }

    private List<DishIngredientDto> combineIngredientQuantities(List<DishIngredientDto> listA, List<DishIngredientDto> listB) {
        Map<Long, Integer> quantityMap = new HashMap<>();

        // Add quantities from listA to the map
        for (DishIngredientDto item : listA) {
            quantityMap.put(item.ingredientId(), item.quantity());
        }

        // Add quantities from listB to the map, summing if necessary
        for (DishIngredientDto item : listB) {
            quantityMap.merge(item.ingredientId(), item.quantity(), Integer::sum);
        }

        // Convert the map back to a list of DishIngredientDto
        return quantityMap.entrySet().stream()
                .map(entry -> new DishIngredientDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    private Long calculateAllIngredientsTotal(List<DishIngredientDto> dishIngredientDtos) {
        AtomicLong totalPrice = new AtomicLong(0L);

        dishIngredientDtos.forEach(dto -> {
            var ingredient = ingredientService.findById(dto.ingredientId());
            totalPrice.addAndGet(ingredient.getSalePrice() * dto.quantity());
        });

        return totalPrice.get();
    }
}
