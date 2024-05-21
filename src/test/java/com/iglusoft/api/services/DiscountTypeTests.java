package com.iglusoft.api.services;

import com.iglusoft.api.database.entities.Dish;
import com.iglusoft.api.database.entities.Ingredient;
import com.iglusoft.api.database.entities.Offer;
import com.iglusoft.api.database.entities.OfferIngredientMinQuantity;
import com.iglusoft.api.dtos.DishIngredientDto;
import com.iglusoft.api.enums.DiscountType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

public class DiscountTypeTests {

    private static final long meatIngredientPrice = 300;
    private static final long cheeseIngredientPrice = 150;

    /**
     * O método {@code assertPercentageDiscountReturnsCorrectValue} é usado para testar se o cálculo do desconto percentual no preço total do prato retorna o valor correto.
     *
     * <p>
     * Este método é anotado com {@code @ParameterizedTest} do framework de testes JUnit 5, indicando que ele será executado várias vezes com diferentes conjuntos de parâmetros de teste.
     * </p>
     *
     * <p>
     * Os parâmetros de teste são fornecidos pelo método {@code providePercentageDiscount}, que retorna uma lista de pares de valores representando o preço total sem desconto e a porcentagem de desconto, respectivamente.
     * </p>
     *
     * <p>
     * Para cada conjunto de parâmetros de teste, este método calcula o resultado esperado multiplicando o preço total sem desconto pela porcentagem de desconto e dividindo por 100.
     * </p>
     *
     * <p>
     * Em seguida, cria uma instância de {@link Offer} e define a quantidade de desconto como a porcentagem fornecida.
     * </p>
     *
     * <p>
     * O método então calcula o resultado atual do cálculo do desconto percentual no preço total do prato usando o método {@code calculateDiscountAmount} do tipo de desconto {@link DiscountType#DISH_TOTAL_PRICE_PERCENTAGE_DISCOUNT}.
     * </p>
     *
     * <p>
     * Finalmente, o método {@code assertEquals} é usado para verificar se o resultado esperado é igual ao resultado atual.
     * </p>
     *
     * @param totalNoDiscount O preço total sem desconto.
     * @param discountPercentage A porcentagem de desconto a ser aplicada.
     */
    @ParameterizedTest
    @MethodSource("providePercentageDiscount")
    void assertPercentageDiscountReturnsCorrectValue(Long totalNoDiscount, Long discountPercentage) {
        var expectedResult = totalNoDiscount / 100 * discountPercentage;
        var offer = new Offer();
        offer.setDiscountAmount(discountPercentage);
        var actualResult = DiscountType.DISH_TOTAL_PRICE_PERCENTAGE_DISCOUNT.calculateDiscountAmount.apply(totalNoDiscount, offer, null);
        assertEquals(expectedResult, actualResult);
    }


    /**
     * O método {@code assertIngredientQuantityDiscountReturnsCorrectValue} é usado para testar se o cálculo do desconto na quantidade de ingredientes retorna o valor correto.
     *
     * <p>
     * Este método é anotado com {@code @ParameterizedTest} do framework de testes JUnit 5, indicando que ele será executado várias vezes com diferentes conjuntos de parâmetros de teste.
     * </p>
     *
     * <p>
     * Os parâmetros de teste são fornecidos pelo método {@code provideIngredientQuantityDiscountArgs}, que retorna uma lista de argumentos de teste para o método de teste.
     * </p>
     *
     * <p>
     * Para cada conjunto de argumentos de teste, este método calcula o resultado esperado e o resultado atual do cálculo do desconto na quantidade de ingredientes usando o método {@code calculateDiscountAmount} do tipo de desconto {@link DiscountType#INGREDIENT_QUANTITY_DISCOUNT}.
     * </p>
     *
     * <p>
     * O método {@code assertEquals} é então usado para verificar se o resultado esperado é igual ao resultado atual.
     * </p>
     *
     * @param expectedResult O resultado esperado do cálculo do desconto na quantidade de ingredientes.
     * @param offer A oferta que contém informações sobre o desconto.
     * @param ingredients A lista de ingredientes relevantes para o cálculo do desconto.
     */
    @ParameterizedTest
    @MethodSource("provideIngredientQuantityDiscountArgs")
    void assertIngredientQuantityDiscountReturnsCorrectValue(Long expectedResult, Offer offer, List<DishIngredientDto> ingredients) {
        var actualResult = DiscountType.INGREDIENT_QUANTITY_DISCOUNT.calculateDiscountAmount.apply(null, offer, ingredients);
        assertEquals(expectedResult, actualResult);
    }


    private static Stream<Arguments> providePercentageDiscount() {
        return Stream.of(
            Arguments.of(100L, 20L),
            Arguments.of(0L, 99L),
            Arguments.of(1467L, 0L)
        );
    }

    private static Stream<Arguments> provideIngredientQuantityDiscountArgs() {
        return Stream.of(
            Arguments.of(meatIngredientPrice, getOfferA(), List.of(new DishIngredientDto(getMeatIngredient().getId(), 3))),
            Arguments.of(meatIngredientPrice*2, getOfferA(), List.of(new DishIngredientDto(getMeatIngredient().getId(), 6))),
            Arguments.of(cheeseIngredientPrice, getOfferB(), List.of(new DishIngredientDto(getCheeseIngredient().getId(), 3))),
            Arguments.of(cheeseIngredientPrice*2, getOfferB(), List.of(new DishIngredientDto(getCheeseIngredient().getId(), 6)))
        );
    }

    // Muita carne
    private static Offer getOfferA() {
        Offer offer = new Offer();
        offer.setName("Muita carne");
        offer.setRequiredIngredients(List.of(
                new OfferIngredientMinQuantity(1L, getMeatIngredient(), 3, 2)
        ));
        return offer;
    }

    // Muito queijo
    private static Offer getOfferB() {
        Offer offer = new Offer();
        offer.setName("Muito queijo");
        offer.setRequiredIngredients(List.of(
                new OfferIngredientMinQuantity(2L, getCheeseIngredient(), 3, 2)
        ));
        return offer;
    }

    private static Ingredient buildIngredient(long id, String name, long price) {
        var ingredient = new Ingredient();
        ingredient.setId(id);
        ingredient.setName(name);
        ingredient.setSalePrice(price);
        return ingredient;
    }

    private static Ingredient getMeatIngredient() {
        return buildIngredient(1, "Carne", meatIngredientPrice);
    }
    private static Ingredient getCheeseIngredient() {
        return buildIngredient(2, "Queijo", cheeseIngredientPrice);
    }

}
