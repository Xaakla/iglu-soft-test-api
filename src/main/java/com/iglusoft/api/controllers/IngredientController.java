package com.iglusoft.api.controllers;

import com.iglusoft.api.dtos.IngredientDto;
import com.iglusoft.api.dtos.NewEditIngredientDto;
import com.iglusoft.api.exceptions.BusinessException;
import com.iglusoft.api.services.IngredientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @PostMapping()
    public ResponseEntity<IngredientDto> newIngredient(@RequestBody @Valid NewEditIngredientDto newEditIngredientDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new IngredientDto(this.ingredientService.saveIngredient(newEditIngredientDto)));
    }

    @GetMapping()
    public ResponseEntity<List<IngredientDto>> findAllIngredients() {
        return ResponseEntity.ok(this.ingredientService.findAllIngredients().stream().map(IngredientDto::new).toList());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<IngredientDto> updateIngredient(@PathVariable Long id, @RequestBody @Valid NewEditIngredientDto newEditIngredientDto) {
        if (!Objects.equals(id, newEditIngredientDto.id())) throw new BusinessException("Invalid ID");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new IngredientDto(this.ingredientService.saveIngredient(newEditIngredientDto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteIngredient(@PathVariable Long id) {
        this.ingredientService.deleteIngredient(id);

        return ResponseEntity.noContent().build();
    }
}
