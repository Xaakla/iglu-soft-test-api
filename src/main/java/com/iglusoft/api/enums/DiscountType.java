package com.iglusoft.api.enums;

import com.iglusoft.api.database.entities.Offer;
import com.iglusoft.api.dtos.DishIngredientDto;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * O enum {@code DiscountType} define os diferentes tipos de descontos disponíveis e fornece métodos para calcular o valor do desconto correspondente.
 *
 * <p>
 * Cada tipo de desconto possui um método associado ({@code calculateDiscountAmount}) que implementa a lógica específica de cálculo do desconto.
 * </p>
 */
public enum DiscountType {

    /**
     * Tipo de desconto: Desconto Percentual no Preço Total do Prato.
     * Calcula o desconto como uma porcentagem do preço total do prato.
     */
    DISH_TOTAL_PRICE_PERCENTAGE_DISCOUNT(
            (totalNoDiscount, offer, ignored) -> Math.max(0L, (totalNoDiscount * offer.getDiscountAmount() / 100))
    ),

    /**
     * Tipo de desconto: Desconto na Quantidade do Ingrediente.
     * Calcula o desconto com base na quantidade de ingredientes necessária e paga especificada na oferta.
     */
    INGREDIENT_QUANTITY_DISCOUNT(
            (ignored, offer, ingredients) -> {
                var discountTotal = new AtomicLong(0L);
                offer.getRequiredIngredients().forEach(offerIngredientMinQuantity -> {
                    var ingredientId = offerIngredientMinQuantity.getIngredient().getId();
                    var ingredientPrice = offerIngredientMinQuantity.getIngredient().getSalePrice();
                    ingredients.stream().filter(it -> Objects.equals(it.ingredientId(), ingredientId)).forEach(ingredient -> {
                        var ingredientTotalNoDiscount = ingredient.quantity() * ingredientPrice;
                        var timesToApply = ingredient.quantity() / offerIngredientMinQuantity.getMinQuantity();
                        var ingredientsToPay = offerIngredientMinQuantity.getPaidQuantity() * timesToApply + (ingredient.quantity() % offerIngredientMinQuantity.getMinQuantity());
                        var ingredientTotalWithDiscount = ingredientsToPay * ingredientPrice;
                        var discountValue = ingredientTotalNoDiscount - ingredientTotalWithDiscount;
                        discountTotal.set(discountTotal.get() + discountValue);
                    });
                });

                return discountTotal.get();
            }
    ),

    // Outros tipos de desconto podem ser adicionados aqui conforme necessário.

    ;

    /**
     * A função de cálculo do desconto associada a cada tipo de desconto.
     */
    public final CalculateDiscountAmount calculateDiscountAmount;

    /**
     * Cria um novo tipo de desconto com a função de cálculo especificada.
     *
     * @param calculateDiscountAmount A função de cálculo do desconto.
     */
    DiscountType(CalculateDiscountAmount calculateDiscountAmount) {
        this.calculateDiscountAmount = calculateDiscountAmount;
    }

    /**
     * Uma interface funcional que define a função de cálculo do desconto.
     */
    public interface CalculateDiscountAmount {
        /**
         * Calcula o valor do desconto com base nos parâmetros fornecidos.
         *
         * @param totalNoDiscount O preço total sem desconto.
         * @param offer A oferta que contém informações sobre o desconto.
         * @param ingredients A lista de ingredientes relevantes para o cálculo do desconto.
         * @return O valor do desconto calculado.
         */
        Long apply(Long totalNoDiscount, Offer offer, List<DishIngredientDto> ingredients);
    }
}

