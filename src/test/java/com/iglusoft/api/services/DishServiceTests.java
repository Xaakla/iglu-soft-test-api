package com.iglusoft.api.services;

import com.iglusoft.api.database.entities.DishIngredientQuantity;
import com.iglusoft.api.database.entities.Ingredient;
import com.iglusoft.api.database.repositories.IngredientRepository;
import com.iglusoft.api.dtos.NewEditDishDto;
import com.iglusoft.api.dtos.NewEditDishIngredientQuantity;
import com.iglusoft.api.exceptions.NotFoundException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class DishServiceTests {

    @Autowired
    private DishService dishService;

    @Autowired
    private IngredientRepository ingredientRepository;

    /**
     * O método {@code assertDishPriceEqualsIngredientSum} é usado para testar se o preço de um prato é igual à soma dos preços de seus ingredientes.
     *
     * <p>
     * Este método é anotado com {@code @ParameterizedTest} do framework de testes JUnit 5, indicando que ele será executado várias vezes com diferentes instâncias de {@code NewEditDishDto} como parâmetro de entrada.
     * </p>
     *
     * <p>
     * Os parâmetros de entrada para este método são fornecidos pelo método {@code provideNewEditDishDto}, que retorna uma lista de instâncias de {@code NewEditDishDto} para teste.
     * </p>
     *
     * <p>
     * Para cada instância de {@code NewEditDishDto} fornecida, este método cria uma lista de ingredientes correspondentes aos ingredientes do prato, recuperando cada ingrediente do repositório de ingredientes e mapeando-os para instâncias de {@code DishIngredientQuantity}.
     * </p>
     *
     * <p>
     * Em seguida, o método calcula o preço total esperado do prato usando o serviço {@code dishService.calculateTotalPrice} com base na lista de ingredientes.
     * </p>
     *
     * <p>
     * O método então salva o prato com os dados fornecidos em {@code newEditDishDto} usando o serviço {@code dishService.saveDish} e obtém o preço total do prato salvo.
     * </p>
     *
     * <p>
     * Finalmente, o método {@code assertEquals} é usado para verificar se o preço total esperado do prato é igual ao preço total do prato salvo, garantindo assim que o preço do prato é igual à soma dos preços de seus ingredientes.
     * </p>
     *
     * @param newEditDishDto O DTO contendo informações sobre o prato a ser testado.
     */
    @ParameterizedTest
    @MethodSource("provideNewEditDishDto")
    void assertDishPriceEqualsIngredientSum(NewEditDishDto newEditDishDto) {

        // Cria uma lista de ingredientes correspondentes aos ingredientes do prato
        var ingredientList = newEditDishDto.ingredientsIds().stream().map(it -> {
            var ingredient = this.ingredientRepository.findById(it.ingredientId()).orElseThrow(NotFoundException::new);
            return new DishIngredientQuantity(null, ingredient, it.quantity());
        }).toList();

        // Calcula o preço total esperado do prato com base na lista de ingredientes
        var expectedResult = dishService.calculateTotalPrice(ingredientList);

        // Salva o prato com os dados fornecidos em newEditDishDto e obtém o preço total do prato salvo
        var actualResult = dishService.saveDish(newEditDishDto).getTotalPrice();

        // Verifica se o preço total esperado do prato é igual ao preço total do prato salvo
        assertEquals(expectedResult, actualResult);
    }


    // Testando regra para calculo de preco de um lanche baseado no preco dos ingredientes
    @ParameterizedTest()
    @MethodSource("provideListOfDishIngredientQuantities")
    void assertCalculatedTotalPriceEqualsIngredientSum(List<DishIngredientQuantity> dishIngredientQuantities) {
        final var expectedResult = dishIngredientQuantities.stream()
                .mapToLong(it -> it.getIngredient().getSalePrice() * it.getQuantity()).sum();

        var actualResult = dishService.calculateTotalPrice(dishIngredientQuantities);
        assertEquals(expectedResult, actualResult);
    }

    private static Stream<Arguments> provideNewEditDishDto() {
        return Stream.of(

            Arguments.of(
                new NewEditDishDto(null, "Test Dish A",
                    List.of(
                        new NewEditDishIngredientQuantity(5, 1L)
                    )
                )
            ),

            Arguments.of(
                new NewEditDishDto(null, "Test Dish B",
                    List.of(
                        new NewEditDishIngredientQuantity(5, 1L),
                        new NewEditDishIngredientQuantity(2, 2L)
                    )
                )
            ),

            Arguments.of(
                new NewEditDishDto(null, "Test Dish C",
                    List.of(
                        new NewEditDishIngredientQuantity(5, 1L),
                        new NewEditDishIngredientQuantity(2, 2L),
                        new NewEditDishIngredientQuantity(1, 3L)
                    )
                )
            )
        );
    }

    private static Stream<Arguments> provideListOfDishIngredientQuantities() {
        Random random = new Random();

        return Stream.of(
            // empty list
            Arguments.of(List.of()),

            // item with 0 price
            Arguments.of(List.of(getDishIngredientQuantity(0L, 0))),

            // list with one item
            Arguments.of(List.of(getDishIngredientQuantity(longPositive(random), intPositive(random)))),

            // list with two items
            Arguments.of(List.of(
                    getDishIngredientQuantity(longPositive(random), intPositive(random)),
                    getDishIngredientQuantity(longPositive(random), intPositive(random)))
            ),

            // list with three items
            Arguments.of(List.of(
                    getDishIngredientQuantity(longPositive(random), intPositive(random)),
                    getDishIngredientQuantity(longPositive(random), intPositive(random))),
                    getDishIngredientQuantity(longPositive(random), intPositive(random))
            )

        );
    }

    private static Ingredient getIngredientWithSalePrice(Long salePrice) {
        var ingredient = new Ingredient();
        ingredient.setSalePrice(salePrice);
        return ingredient;
    }

    private static DishIngredientQuantity getDishIngredientQuantity(Long salePrice, int quantity) {
        return new DishIngredientQuantity(null, getIngredientWithSalePrice(salePrice), quantity);
    }

    private static Long longPositive(Random random) {
        return 1L + (Math.abs(random.nextLong()) % 1000L); // Generate a random positive long between 1 and 1000
    }

    private static int intPositive(Random random) {
        return 1 + random.nextInt(100); // Generate a random positive int between 1 and 100
    }

}
