package com.iglusoft.api.services;

import com.iglusoft.api.commons.ObjectValidationResponse;
import com.iglusoft.api.dtos.NewEditIngredientDto;
import com.iglusoft.api.database.entities.Ingredient;
import com.iglusoft.api.exceptions.BusinessException;
import com.iglusoft.api.exceptions.NotFoundException;
import com.iglusoft.api.interfaces.IValidatesObject;
import com.iglusoft.api.database.repositories.DishIngredientQuantityRepository;
import com.iglusoft.api.database.repositories.IngredientRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IngredientService implements IValidatesObject<Ingredient> {
    private final IngredientRepository ingredientRepository;
    private final DishIngredientQuantityRepository dishIngredientQuantityRepository;

    public IngredientService(IngredientRepository ingredientRepository, DishIngredientQuantityRepository dishIngredientQuantityRepository) {
        this.ingredientRepository = ingredientRepository;
        this.dishIngredientQuantityRepository = dishIngredientQuantityRepository;
    }

    @Transactional
    public Ingredient saveIngredient(NewEditIngredientDto newEditIngredientDto) {
        boolean isEdit = newEditIngredientDto.id() != null;
        var ingredientToSave = isEdit ?
                ingredientRepository.findById(newEditIngredientDto.id())
                        .orElseThrow(NotFoundException::new) : new Ingredient();

        ingredientToSave.setName(newEditIngredientDto.name());
        ingredientToSave.setSalePrice(newEditIngredientDto.salePrice());

        var validationResponse = validate(ingredientToSave);
        if (validationResponse.isInvalid())
            throw new BusinessException(validationResponse.message());

        return this.ingredientRepository.save(ingredientToSave);
    }

    public List<Ingredient> findAllIngredients() {
        return this.ingredientRepository.findAll();
    }

    public Ingredient findById(Long id) {
        return this.ingredientRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public void deleteIngredient(Long id) {
        if (!this.ingredientRepository.existsById(id))
            throw new NotFoundException();

        if (this.dishIngredientQuantityRepository.existsByIngredientId(id))
            throw new BusinessException(String.format("Cannot delete the ingredient with id '%d' because it is being used by a dish.", id));

        this.ingredientRepository.deleteById(id);
    }

    /**
     * Valida um objeto {@link Ingredient} com base em várias regras de negócios.
     *
     * @param ingredient O objeto {@link Ingredient} a ser validado.
     * @return Um {@link ObjectValidationResponse} indicando se a validação foi bem-sucedida ou falhou,
     *         juntamente com uma mensagem apropriada.
     *
     * <p>
     * Este método verifica se:
     * </p>
     * <ul>
     *     <li>O campo 'name' não está vazio.</li>
     *     <li>O campo 'salePrice' é maior que zero.</li>
     *     <li>Não existe outro ingrediente com o mesmo nome e um ID diferente no repositório.</li>
     * </ul>
     *
     * <p><strong>Exemplo de uso:</strong></p>
     * <pre>{@code
     * Ingredient ingredient = new Ingredient();
     * ingredient.setName("Tomato");
     * ingredient.setSalePrice(3L);
     * ingredient.setId(1L);
     *
     * ObjectValidationResponse response = validate(ingredient);
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
     *     <li>A verificação de existência de um ingrediente com o mesmo nome e ID diferente no repositório para garantir a unicidade do nome do ingrediente.</li>
     *     <li>Uso de `Optional.ofNullable` para tratar o caso de ID nulo, garantindo que um valor padrão de 0 seja usado.</li>
     * </ul>
     */
    @Override
    public ObjectValidationResponse validate(Ingredient ingredient) {
        if (ingredient.getName().isEmpty()) {
            return new ObjectValidationResponse(false, "Field 'name' cannot be empty.");
        }
        if (ingredient.getSalePrice() <= 0) {
            return new ObjectValidationResponse(false, "Field 'salePrice' cannot be less or equal than zero.");
        }
        if (this.ingredientRepository.existsByNameAndIdNot(ingredient.getName(), Optional.ofNullable(ingredient.getId()).orElse(0L))) {
            return new ObjectValidationResponse(false, String.format("Ingredient with name '%s' already exists.", ingredient.getName()));
        }

        return new ObjectValidationResponse(true, "Validated successfully.");
    }

}