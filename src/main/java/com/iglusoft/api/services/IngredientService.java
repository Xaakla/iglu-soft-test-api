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