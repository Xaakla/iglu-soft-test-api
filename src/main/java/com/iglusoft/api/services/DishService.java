package com.iglusoft.api.services;

import com.iglusoft.api.commons.ObjectValidationResponse;
import com.iglusoft.api.database.entities.Dish;
import com.iglusoft.api.database.entities.DishIngredientQuantity;
import com.iglusoft.api.database.repositories.DishRepository;
import com.iglusoft.api.database.repositories.IngredientRepository;
import com.iglusoft.api.dtos.NewEditDishDto;
import com.iglusoft.api.exceptions.BusinessException;
import com.iglusoft.api.exceptions.NotFoundException;
import com.iglusoft.api.interfaces.IValidatesObject;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class DishService implements IValidatesObject<Dish> {
    private final DishRepository dishRepository;
    private final IngredientRepository ingredientRepository;

    public DishService(
            DishRepository dishRepository,
            IngredientRepository ingredientRepository) {
        this.dishRepository = dishRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public Dish findById(long id) {
        return dishRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Dish saveDish(NewEditDishDto newEditDishDto) {
        boolean isEdit = newEditDishDto.id() != null;
        var dishToSave = isEdit ?
                dishRepository.findById(newEditDishDto.id())
                        .orElseThrow(NotFoundException::new) : new Dish();

        dishToSave.setName(newEditDishDto.name());

        dishToSave.getIngredients().clear();
        dishToSave.getIngredients().addAll(newEditDishDto.ingredientsIds().stream().map(it -> {
            var ingredient = this.ingredientRepository.findById(it.ingredientId()).orElseThrow(NotFoundException::new);
            return new DishIngredientQuantity(dishToSave, ingredient, it.quantity());
        }).toList());

        dishToSave.setTotalPrice(calculateTotalPrice(dishToSave.getIngredients()));
        var response = this.validate(dishToSave);
        if (response.isInvalid())
            throw new BusinessException(response.message());

        return this.dishRepository.save(dishToSave);
    }

    public List<Dish> findAllDishes() {
        return this.dishRepository.findAll();
    }

    @Transactional
    public void deleteDish(Long id) {
        if (!this.dishRepository.existsById(id))
            throw new NotFoundException();

        this.dishRepository.deleteById(id);
    }

    /**
     * Calcula o preço total de uma lista de ingredientes de prato com suas quantidades respectivas.
     *
     * @param dishIngredientQuantities Lista de objetos {@link DishIngredientQuantity} que representam
     *                                 os ingredientes de um prato e suas quantidades associadas.
     * @return O preço total calculado como a soma dos preços de venda de cada ingrediente multiplicado
     *         pela sua quantidade correspondente.
     *
     * <p>
     * Cada objeto {@link DishIngredientQuantity} na lista contém um ingrediente e sua quantidade. A função
     * itera sobre a lista, calcula o preço total para cada ingrediente (preço de venda do ingrediente multiplicado
     * pela quantidade), e adiciona esse valor ao preço total usando um {@link AtomicLong} para garantir a segurança
     * em ambientes multithreaded.
     * </p>
     *
     * <p><strong>Exemplo de uso:</strong></p>
     * <pre>{@code
     * List<DishIngredientQuantity> ingredients = Arrays.asList(
     *     new DishIngredientQuantity(new Ingredient("Tomato", 2L), 3),
     *     new DishIngredientQuantity(new Ingredient("Cheese", 5L), 2)
     * );
     * Long total = calculateTotalPrice(ingredients);
     * System.out.println("Total Price: " + total);  // Output: Total Price: 16
     * }</pre>
     *
     * <p><strong>Decisões de Design:</strong></p>
     * <ul>
     *     <li>Utilização de {@link AtomicLong} para assegurar a segurança de threads, caso a função seja chamada
     *     em um ambiente multithreaded.</li>
     *     <li>Uso de streams para simplificar a iteração e acumulação dos valores.</li>
     * </ul>
     */
    public Long calculateTotalPrice(List<DishIngredientQuantity> dishIngredientQuantities) {
        AtomicLong totalPrice = new AtomicLong(0L);

        dishIngredientQuantities.forEach(dishIngredientQuantity -> {
            totalPrice.addAndGet(dishIngredientQuantity.getIngredient().getSalePrice() * dishIngredientQuantity.getQuantity());
        });

        return totalPrice.get();
    }

    /**
     * Valida um objeto {@link Dish} com base em várias regras de negócios.
     *
     * @param dish O objeto {@link Dish} a ser validado.
     * @return Um {@link ObjectValidationResponse} indicando se a validação foi bem-sucedida ou falhou,
     *         juntamente com uma mensagem apropriada.
     *
     * <p>
     * Este método verifica se:
     * </p>
     * <ul>
     *     <li>O campo 'name' não está vazio.</li>
     *     <li>O campo 'totalPrice' é maior que zero.</li>
     *     <li>Não existe outro prato com o mesmo nome e um ID diferente no repositório.</li>
     * </ul>
     *
     * <p><strong>Exemplo de uso:</strong></p>
     * <pre>{@code
     * Dish dish = new Dish();
     * dish.setName("Pasta");
     * dish.setTotalPrice(15L);
     * dish.setId(1L);
     *
     * ObjectValidationResponse response = validate(dish);
     * if (response.isValid()) {
     *     System.out.println("Validation successful: " + response.getMessage());
     * } else {
     *     System.out.println("Validation failed: " + response.getMessage());
     * }
     * }</pre>
     *
     * <p><strong>Decisões de Design:</strong></p>
     * <ul>
     *     <li>O uso de mensagens específicas de erro para fornecer feedback claro sobre falhas de validação.</li>
     *     <li>A verificação de existência de um prato com o mesmo nome e ID diferente no repositório para garantir a unicidade do nome do prato.</li>
     * </ul>
     */
    @Override
    public ObjectValidationResponse validate(Dish dish) {
        if (dish.getName().isEmpty()) {
            return new ObjectValidationResponse(false, "Field 'name' cannot be empty.");
        }
        if (dish.getTotalPrice() <= 0) {
            return new ObjectValidationResponse(false, "Field 'totalPrice' cannot be less or equal than zero.");
        }
        if (this.dishRepository.existsByNameAndIdNot(dish.getName(), dish.getId())) {
            return new ObjectValidationResponse(false, String.format("Dish with name '%s' already exists.", dish.getName()));
        }

        return new ObjectValidationResponse(true, "Validated successfully.");
    }

}
