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

    /**
     * Retorna a resposta do pedido com o preço total e os detalhes de cada prato no pedido.
     *
     * @param orders Uma lista de objetos {@link DishOrderDto} contendo as informações de cada prato no pedido.
     * @return Um objeto {@link OrderResponseDto} contendo o preço total do pedido e os detalhes de cada prato.
     *
     * <p>
     * Este método realiza as seguintes operações:
     * </p>
     * <ul>
     *     <li>Para cada prato no pedido, calcula o preço final do prato usando {@link #calculateDishOrderFinalPrice(DishOrderDto)}.</li>
     *     <li>Armazena o preço final calculado de cada prato em um mapa, onde a chave é o ID do prato.</li>
     *     <li>Calcula o preço total do pedido somando os preços finais de todos os pratos.</li>
     *     <li>Retorna um objeto {@link OrderResponseDto} contendo o preço total do pedido e os detalhes de cada prato.</li>
     * </ul>
     *
     * <p><strong>Decisões de Design:</strong></p>
     * <ul>
     *     <li>Uso de um mapa para armazenar os preços finais calculados de cada prato, facilitando a recuperação dos detalhes de cada prato pelo ID do prato.</li>
     *     <li>Utilização de um loop tradicional para iterar sobre cada prato no pedido, uma vez que não é necessário processar os elementos em paralelo ou modificar a coleção original.</li>
     *     <li>Calculo do preço total do pedido usando streams e a função de soma {@link LongStream#sum()} para uma implementação concisa e eficiente.</li>
     *     <li>Utilização de objetos DTO para representar os detalhes do pedido e dos pratos, promovendo uma comunicação clara e desacoplada.</li>
     * </ul>
     */
    public OrderResponseDto getOrderResponse(List<DishOrderDto> orders) {
        var dishIdToTotalPriceMap = new HashMap<Long, OrderDishResponseDto>();
        for (DishOrderDto order : orders) {
            dishIdToTotalPriceMap.put(order.dishId(), calculateDishOrderFinalPrice(order));
        }
        var totalPrice = dishIdToTotalPriceMap.values().stream().mapToLong(OrderDishResponseDto::salePrice).sum();
        return new OrderResponseDto(totalPrice, new ArrayList<>(dishIdToTotalPriceMap.values()));
    }


    /**
     * Calcula o preço final de um prato em um pedido, considerando descontos de ofertas aplicáveis.
     *
     * @param order O objeto {@link DishOrderDto} que contém as informações do pedido, incluindo o ID do prato e os ingredientes selecionados.
     * @return Um objeto {@link OrderDishResponseDto} contendo o nome do prato, o preço final e os detalhes dos ingredientes no pedido.
     *
     * <p>
     * Este método realiza as seguintes operações:
     * </p>
     * <ul>
     *     <li>Obtém o objeto {@link Dish} correspondente ao ID do prato no pedido usando {@link DishService#findById(Long)}.</li>
     *     <li>Obtém a lista original de ingredientes do prato e as quantidades associadas, mapeando-as para objetos {@link DishIngredientDto} usando {@link Stream#map(Function)}.</li>
     *     <li>Combina as quantidades de ingredientes originais do prato com as quantidades de ingredientes selecionadas no pedido usando {@link #combineIngredientQuantities(List, List)}.</li>
     *     <li>Calcula o preço total de todos os ingredientes sem desconto usando {@link #calculateAllIngredientsTotal(List)}.</li>
     *     <li>Obtém uma lista de ofertas válidas aplicáveis ao pedido, excluindo as que possuem ingredientes excluídos e garantindo que todos os ingredientes necessários estejam presentes usando {@link #getValidOffersForIngredientsList(List)}.</li>
     *     <li>Para cada oferta válida, calcula o valor do desconto e ajusta o preço total do prato, garantindo que o preço final não seja negativo.</li>
     *     <li>Mapeia os detalhes dos ingredientes no pedido para objetos {@link OrderIngredientResponseDto}, contendo o nome do ingrediente e sua quantidade.</li>
     *     <li>Retorna um objeto {@link OrderDishResponseDto} contendo o nome do prato, o preço final e os detalhes dos ingredientes no pedido.</li>
     * </ul>
     *
     * <p><strong>Decisões de Design:</strong></p>
     * <ul>
     *     <li>Uso de métodos auxiliares para modularizar a lógica e promover a reutilização de código.</li>
     *     <li>Uso de streams e operações de mapeamento para processar e manipular as coleções de dados de forma concisa e eficiente.</li>
     *     <li>Cálculo do desconto de oferta e ajuste do preço total do prato dentro de um loop para garantir que todos os descontos sejam aplicados corretamente.</li>
     * </ul>
     */
    public OrderDishResponseDto calculateDishOrderFinalPrice(DishOrderDto order) {
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


    /**
     * Retorna uma lista de ofertas válidas com base na lista de quantidades de ingredientes do prato.
     *
     * @param ingredientQuantities Uma lista de objetos {@link DishIngredientDto} que representam as quantidades de ingredientes no pedido de prato.
     * @return Uma lista de objetos {@link Offer} que representam as ofertas válidas para a lista de quantidades de ingredientes do prato.
     *
     * <p>
     * Este método realiza as seguintes operações:
     * </p>
     * <ul>
     *     <li>Obtém todas as ofertas disponíveis usando {@link OfferService#findAllOffers()}.</li>
     *     <li>Filtra as ofertas com base em dois critérios:
     *         <ul>
     *             <li>Exclui as ofertas que contêm ingredientes excluídos pelo pedido de prato usando {@link #ingredientListContainsAnyOfferExcludedIngredient(Offer, List)}.</li>
     *             <li>Seleciona apenas as ofertas que contêm todos os ingredientes necessários pelo pedido de prato usando
     *             {@link #ingredientListContainsAllOfferRequiredIngredients(Offer, List)}.</li>
     *         </ul>
     *     </li>
     *     <li>Retorna a lista filtrada de ofertas válidas.</li>
     * </ul>
     *
     * <p><strong>Exemplo de uso:</strong></p>
     * <pre>{@code
     * List<DishIngredientDto> ingredientQuantities = Arrays.asList(
     *     new DishIngredientDto(1L, 2),
     *     new DishIngredientDto(2L, 4),
     *     new DishIngredientDto(3L, 1)
     * );
     *
     * List<Offer> validOffers = getValidOffersForIngredientsList(ingredientQuantities);
     * System.out.println("Valid Offers: " + validOffers);
     * }</pre>
     *
     * <p><strong>Decisões de Design:</strong></p>
     * <ul>
     *     <li>Utilização de {@link Stream#filter(Predicate)} para filtrar as ofertas com base nos critérios definidos.</li>
     *     <li>Reutilização dos métodos {@link #ingredientListContainsAnyOfferExcludedIngredient(Offer, List)} e
     *     {@link #ingredientListContainsAllOfferRequiredIngredients(Offer, List)} para verificar se as ofertas são válidas.</li>
     * </ul>
     */
    private List<Offer> getValidOffersForIngredientsList(List<DishIngredientDto> ingredientQuantities) {
        return offerService.findAllOffers().stream()
                .filter(offer -> !ingredientListContainsAnyOfferExcludedIngredient(offer, ingredientQuantities))
                .filter(offer -> ingredientListContainsAllOfferRequiredIngredients(offer, ingredientQuantities))
                .toList();
    }


    /**
     * Verifica se a lista de ingredientes de um prato contém algum ingrediente excluído por uma oferta específica.
     *
     * @param offer             O objeto {@link Offer} contendo as informações da oferta, incluindo os ingredientes excluídos.
     * @param ingredientQuantities Uma lista de objetos {@link DishIngredientDto} que representam as quantidades de ingredientes no pedido de prato.
     * @return true se a lista de ingredientes contiver algum ingrediente excluído pela oferta, caso contrário, false.
     *
     * <p>
     * Este método realiza as seguintes operações:
     * </p>
     * <ul>
     *     <li>Itera sobre a lista de ingredientes excluídos na oferta usando {@link Stream#anyMatch(Predicate)}.</li>
     *     <li>Verifica se pelo menos um ingrediente excluído está presente na lista de ingredientes do pedido de prato usando
     *     {@link #dishOrderContainsIngredient(List, OfferIngredientMinQuantity)}.</li>
     *     <li>Retorna true se pelo menos um ingrediente excluído estiver presente na lista de ingredientes do pedido de prato,
     *     indicando que o prato contém ingredientes que devem ser excluídos de acordo com a oferta. Caso contrário, retorna false.</li>
     * </ul>
     *
     * <p><strong>Exemplo de uso:</strong></p>
     * <pre>{@code
     * Offer offer = new Offer();
     * List<OfferIngredientMinQuantity> excludedIngredients = Arrays.asList(
     *     new OfferIngredientMinQuantity(new Ingredient(1L), 2),
     *     new OfferIngredientMinQuantity(new Ingredient(2L), 3)
     * );
     * offer.setExcludedIngredients(excludedIngredients);
     *
     * List<DishIngredientDto> ingredientQuantities = Arrays.asList(
     *     new DishIngredientDto(1L, 2),
     *     new DishIngredientDto(3L, 4),
     *     new DishIngredientDto(4L, 1)
     * );
     *
     * boolean containsExcludedIngredient = ingredientListContainsAnyOfferExcludedIngredient(offer, ingredientQuantities);
     * System.out.println("Contains Excluded Ingredient: " + containsExcludedIngredient); // Output: Contains Excluded Ingredient: true
     * }</pre>
     *
     * <p><strong>Decisões de Design:</strong></p>
     * <ul>
     *     <li>Uso de {@link Stream#anyMatch(Predicate)} para verificar se pelo menos um ingrediente excluído está presente na lista de ingredientes do pedido.</li>
     *     <li>Reutilização do método {@link #dishOrderContainsIngredient(List, OfferIngredientMinQuantity)} para verificar se um ingrediente está presente na lista de ingredientes do pedido.</li>
     *     <li>Retorno imediato de true se algum ingrediente excluído estiver presente na lista de ingredientes do pedido, otimizando a verificação.</li>
     * </ul>
     */
    private boolean ingredientListContainsAnyOfferExcludedIngredient(Offer offer, List<DishIngredientDto> ingredientQuantities) {
        return offer.getExcludedIngredients().stream().anyMatch(excluded -> dishOrderContainsIngredient(ingredientQuantities, excluded));
    }


    /**
     * Verifica se a lista de ingredientes de um prato contém todos os ingredientes necessários para uma oferta específica.
     *
     * @param offer             O objeto {@link Offer} contendo as informações da oferta, incluindo os ingredientes necessários.
     * @param ingredientQuantities Uma lista de objetos {@link DishIngredientDto} que representam as quantidades de ingredientes no pedido de prato.
     * @return true se a lista de ingredientes contiver todos os ingredientes necessários para a oferta, caso contrário, false.
     *
     * <p>
     * Este método realiza as seguintes operações:
     * </p>
     * <ul>
     *     <li>Itera sobre a lista de ingredientes necessários na oferta usando {@link Stream#allMatch(Predicate)}.</li>
     *     <li>Verifica se todos os ingredientes necessários estão presentes na lista de ingredientes do pedido de prato usando
     *     {@link #dishOrderContainsIngredient(List, OfferIngredientMinQuantity)}.</li>
     *     <li>Retorna true se todos os ingredientes necessários estiverem presentes na lista de ingredientes do pedido de prato,
     *     indicando que o prato atende aos requisitos da oferta. Caso contrário, retorna false.</li>
     * </ul>
     *
     * <p><strong>Exemplo de uso:</strong></p>
     * <pre>{@code
     * Offer offer = new Offer();
     * List<OfferIngredientMinQuantity> requiredIngredients = Arrays.asList(
     *     new OfferIngredientMinQuantity(new Ingredient(1L), 2),
     *     new OfferIngredientMinQuantity(new Ingredient(2L), 3)
     * );
     * offer.setRequiredIngredients(requiredIngredients);
     *
     * List<DishIngredientDto> ingredientQuantities = Arrays.asList(
     *     new DishIngredientDto(1L, 2),
     *     new DishIngredientDto(2L, 4),
     *     new DishIngredientDto(3L, 1)
     * );
     *
     * boolean containsAllRequiredIngredients = ingredientListContainsAllOfferRequiredIngredients(offer, ingredientQuantities);
     * System.out.println("Contains All Required Ingredients: " + containsAllRequiredIngredients); // Output: Contains All Required Ingredients: false
     * }</pre>
     *
     * <p><strong>Decisões de Design:</strong></p>
     * <ul>
     *     <li>Uso de {@link Stream#allMatch(Predicate)} para verificar se todos os ingredientes necessários estão presentes na lista de ingredientes do pedido.</li>
     *     <li>Reutilização do método {@link #dishOrderContainsIngredient(List, OfferIngredientMinQuantity)} para verificar se um ingrediente está presente na lista de ingredientes do pedido.</li>
     *     <li>Retorno imediato de false se algum ingrediente necessário estiver ausente na lista de ingredientes do pedido, otimizando a verificação.</li>
     * </ul>
     */
    private boolean ingredientListContainsAllOfferRequiredIngredients(Offer offer, List<DishIngredientDto> ingredientQuantities) {
        return offer.getRequiredIngredients().stream().allMatch(required -> dishOrderContainsIngredient(ingredientQuantities, required));
    }


    /**
     * Verifica se um pedido de prato contém um ingrediente com as quantidades mínimas exigidas por uma oferta.
     *
     * @param ingredientQuantities      Uma lista de objetos {@link DishIngredientDto} que representam as quantidades de ingredientes no pedido.
     * @param offerIngredientMinQuantity O objeto {@link OfferIngredientMinQuantity} que contém as quantidades mínimas exigidas pelo ingrediente na oferta.
     * @return true se o pedido de prato contiver o ingrediente com as quantidades mínimas exigidas pela oferta, caso contrário, false.
     *
     * <p>
     * Este método realiza as seguintes operações:
     * </p>
     * <ul>
     *     <li>Itera sobre a lista de {@code ingredientQuantities} usando {@link Stream#anyMatch(Predicate)}.</li>
     *     <li>Verifica se algum dos ingredientes na lista tem o mesmo ID que o ingrediente mínimo da oferta e se a quantidade do ingrediente
     *     é igual ou superior à quantidade mínima exigida pela oferta.</li>
     *     <li>Retorna true se uma correspondência for encontrada, indicando que o pedido de prato contém o ingrediente com as quantidades mínimas
     *     exigidas pela oferta. Caso contrário, retorna false.</li>
     * </ul>
     *
     * <p><strong>Exemplo de uso:</strong></p>
     * <pre>{@code
     * List<DishIngredientDto> ingredientQuantities = Arrays.asList(
     *     new DishIngredientDto(1L, 3),
     *     new DishIngredientDto(2L, 5),
     *     new DishIngredientDto(3L, 2)
     * );
     *
     * OfferIngredientMinQuantity offerIngredientMinQuantity = new OfferIngredientMinQuantity();
     * offerIngredientMinQuantity.setIngredient(new Ingredient(1L)); // Ingrediente com ID 1
     * offerIngredientMinQuantity.setMinQuantity(2); // Quantidade mínima exigida: 2
     *
     * boolean containsIngredient = dishOrderContainsIngredient(ingredientQuantities, offerIngredientMinQuantity);
     * System.out.println("Contains Ingredient: " + containsIngredient); // Output: Contains Ingredient: true
     * }</pre>
     *
     * <p><strong>Decisões de Design:</strong></p>
     * <ul>
     *     <li>Uso de {@link Stream#anyMatch(Predicate)} para verificar se pelo menos um ingrediente na lista atende às condições especificadas.</li>
     *     <li>Verificação de igualdade de IDs usando {@link Objects#equals(Object, Object)} para garantir uma comparação segura e robusta.</li>
     *     <li>Verificação da quantidade mínima exigida pela oferta para determinar se o ingrediente atende aos critérios.</li>
     * </ul>
     */
    private boolean dishOrderContainsIngredient(List<DishIngredientDto> ingredientQuantities, OfferIngredientMinQuantity offerIngredientMinQuantity) {
        return ingredientQuantities.stream().anyMatch(ingredient ->
                Objects.equals(ingredient.ingredientId(), offerIngredientMinQuantity.getIngredient().getId()) &&
                        ingredient.quantity() >= offerIngredientMinQuantity.getMinQuantity()
        );
    }


    /**
     * Combina as quantidades de ingredientes de duas listas de {@link DishIngredientDto} em uma única lista.
     *
     * @param listA A primeira lista de {@link DishIngredientDto} a ser combinada.
     * @param listB A segunda lista de {@link DishIngredientDto} a ser combinada.
     * @return Uma lista de {@link DishIngredientDto} que representa a combinação das quantidades de ingredientes das duas listas.
     *
     * <p>
     * Este método realiza as seguintes operações:
     * </p>
     * <ul>
     *     <li>Cria um mapa {@link HashMap} para armazenar as quantidades de ingredientes.</li>
     *     <li>Adiciona as quantidades da listaA ao mapa.</li>
     *     <li>Adiciona as quantidades da listaB ao mapa, somando as quantidades se o ingrediente já estiver presente.</li>
     *     <li>Converte o mapa de volta para uma lista de {@link DishIngredientDto}.</li>
     * </ul>
     *
     * <p><strong>Exemplo de uso:</strong></p>
     * <pre>{@code
     * List<DishIngredientDto> listA = Arrays.asList(
     *     new DishIngredientDto(1L, 2),
     *     new DishIngredientDto(2L, 3)
     * );
     *
     * List<DishIngredientDto> listB = Arrays.asList(
     *     new DishIngredientDto(1L, 3),
     *     new DishIngredientDto(3L, 1)
     * );
     *
     * List<DishIngredientDto> combinedList = combineIngredientQuantities(listA, listB);
     * combinedList.forEach(dto -> System.out.println("Ingredient ID: " + dto.ingredientId() + ", Quantity: " + dto.quantity()));
     * }</pre>
     *
     * <p><strong>Decisões de Design:</strong></p>
     * <ul>
     *     <li>Uso de um mapa {@link HashMap} para armazenar as quantidades de ingredientes, garantindo que cada ingrediente
     *     tenha apenas uma entrada e facilitando a atualização das quantidades.</li>
     *     <li>Uso de {@link Map#merge} para adicionar as quantidades da listaB ao mapa, somando as quantidades se o ingrediente
     *     já estiver presente.</li>
     *     <li>Conversão do mapa de volta para uma lista de {@link DishIngredientDto} para representar a combinação das quantidades
     *     de ingredientes das duas listas.</li>
     * </ul>
     */
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


    /**
     * Calcula o preço total de todos os ingredientes de um prato com base nos dados fornecidos.
     *
     * @param dishIngredientDtos Uma lista de objetos {@link DishIngredientDto} que contêm os IDs e as quantidades dos ingredientes.
     * @return O preço total de todos os ingredientes do prato.
     *
     * <p>
     * Este método realiza as seguintes operações:
     * </p>
     * <ul>
     *     <li>Cria uma variável {@link AtomicLong} para armazenar o preço total.</li>
     *     <li>Itera sobre cada objeto {@link DishIngredientDto} na lista fornecida.</li>
     *     <li>Para cada objeto, busca o ingrediente pelo ID usando o serviço {@code ingredientService}.</li>
     *     <li>Adiciona o preço de venda do ingrediente multiplicado pela quantidade ao preço total.</li>
     * </ul>
     *
     * <p><strong>Exemplo de uso:</strong></p>
     * <pre>{@code
     * List<DishIngredientDto> dishIngredients = Arrays.asList(
     *     new DishIngredientDto(1L, 2),
     *     new DishIngredientDto(2L, 3)
     * );
     *
     * Long total = calculateAllIngredientsTotal(dishIngredients);
     * System.out.println("Total Price: " + total);  // Exibe o preço total calculado
     * }</pre>
     *
     * <p><strong>Decisões de Design:</strong></p>
     * <ul>
     *     <li>Uso de {@link AtomicLong} para manipulação segura de threads ao calcular o preço total.</li>
     *     <li>Busca de ingredientes usando o serviço {@code ingredientService} para garantir que os dados estejam atualizados e corretos.</li>
     *     <li>Uso de `forEach` para iterar sobre a lista de ingredientes e calcular o preço total de forma eficiente.</li>
     * </ul>
     */
    private Long calculateAllIngredientsTotal(List<DishIngredientDto> dishIngredientDtos) {
        AtomicLong totalPrice = new AtomicLong(0L);

        dishIngredientDtos.forEach(dto -> {
            var ingredient = ingredientService.findById(dto.ingredientId());
            totalPrice.addAndGet(ingredient.getSalePrice() * dto.quantity());
        });

        return totalPrice.get();
    }

}
