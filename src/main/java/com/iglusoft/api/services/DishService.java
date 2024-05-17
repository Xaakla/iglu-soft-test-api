package com.iglusoft.api.services;

import com.iglusoft.api.commons.ObjectValidationResponse;
import com.iglusoft.api.dtos.NewEditDishDto;
import com.iglusoft.api.database.entities.Dish;
import com.iglusoft.api.database.entities.DishIngredientQuantity;
import com.iglusoft.api.exceptions.BusinessException;
import com.iglusoft.api.exceptions.NotFoundException;
import com.iglusoft.api.interfaces.IValidatesObject;
import com.iglusoft.api.database.repositories.DishRepository;
import com.iglusoft.api.database.repositories.IngredientRepository;
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
            IngredientRepository ingredientRepository
    ) {
        this.dishRepository = dishRepository;
        this.ingredientRepository = ingredientRepository;
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

    private Long calculateTotalPrice(List<DishIngredientQuantity> dishIngredientQuantities) {
        AtomicLong totalPrice = new AtomicLong(0L);

        dishIngredientQuantities.forEach((DishIngredientQuantity dishIngredientQuantity) -> {
            totalPrice.addAndGet(dishIngredientQuantity.getIngredient().getSalePrice() * dishIngredientQuantity.getQuantity());
        });

        return totalPrice.get();
    }

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
