package com.iglusoft.api.services;

import com.iglusoft.api.dtos.DishIngredientDto;
import com.iglusoft.api.dtos.DishOrderDto;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class OrderServiceTests {

    @Autowired
    private OrderService orderService;


    // Teste para garantir que a promocao está sendo aplicada corretamente no pedido
    /**
     * O método {@code assertCalculateDishOrderFinalPriceAppliesDiscount} é usado para testar se o cálculo do preço final do pedido de prato aplica corretamente os descontos.
     *
     * <p>
     * Este método é anotado com {@code @ParameterizedTest} do framework de testes JUnit 5, indicando que ele será executado várias vezes com diferentes instâncias de {@code DishOrderDto} como parâmetro de entrada.
     * </p>
     *
     * <p>
     * Os parâmetros de entrada para este método são fornecidos pelo método {@code provideDishOrderDto}, que retorna uma lista de instâncias de {@code DishOrderDto} para teste.
     * </p>
     *
     * <p>
     * Para cada instância de {@code DishOrderDto} fornecida, este método calcula o preço final do pedido de prato usando o serviço {@code orderService.calculateDishOrderFinalPrice}.
     * </p>
     *
     * <p>
     * O método então compara o preço final calculado com o preço final esperado e verifica se eles são iguais usando o método {@code assertEquals}.
     * </p>
     *
     * @param order O DTO contendo informações sobre o pedido de prato a ser testado.
     * @param expectedTotalPrice O preço final esperado do pedido de prato.
     */
    @ParameterizedTest
    @MethodSource("provideDishOrderDto")
    void assertCalculateDishOrderFinalPriceAppliesDiscount(DishOrderDto order, Long expectedTotalPrice) {
        // Calcula o preço final do pedido de prato usando o serviço orderService.calculateDishOrderFinalPrice
        var actualResult = orderService.calculateDishOrderFinalPrice(order).salePrice();

        // Verifica se o preço final calculado é igual ao preço final esperado
        assertEquals(expectedTotalPrice, actualResult);
    }


    private static Stream<Arguments> provideDishOrderDto() {
        return Stream.of(
            Arguments.of(new DishOrderDto(1L, List.of()), 510L),

            Arguments.of(
                new DishOrderDto(1L,
                    List.of(
                        new DishIngredientDto(1L, 1)
                    )
                ),
                520L
            ),

            Arguments.of(
                new DishOrderDto(2L,
                    List.of(
                        new DishIngredientDto(1L, 5),
                        new DishIngredientDto(2L, 5),
                        new DishIngredientDto(3L, 1)
                    )
                ),
                230L
            )
        );
    }


}
