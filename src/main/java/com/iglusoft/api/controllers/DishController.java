package com.iglusoft.api.controllers;

import com.iglusoft.api.dtos.DishDto;
import com.iglusoft.api.dtos.NewEditDishDto;
import com.iglusoft.api.exceptions.BusinessException;
import com.iglusoft.api.services.DishService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/dishes")
public class DishController {
    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @PostMapping()
    public ResponseEntity<DishDto> newDish(@RequestBody @Valid NewEditDishDto newEditDish) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new DishDto(this.dishService.saveDish(newEditDish)));
    }

    @GetMapping()
    public ResponseEntity<List<DishDto>> findAllDishes() {
        return ResponseEntity.ok(this.dishService.findAllDishes().stream().map(DishDto::new).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DishDto> findDishById(@PathVariable long id) {
        return ResponseEntity.ok(new DishDto(this.dishService.findById(id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateDish(@PathVariable Long id, @RequestBody @Valid NewEditDishDto newEditDish) {
        if (!Objects.equals(id, newEditDish.id())) throw new BusinessException("Invalid ID");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new DishDto(this.dishService.saveDish(newEditDish)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteDish(@PathVariable Long id) {
        this.dishService.deleteDish(id);

        return ResponseEntity.noContent().build();
    }
}
