package com.iglusoft.api.controllers;

import com.iglusoft.api.dtos.NewEditOffer;
import com.iglusoft.api.dtos.OfferDto;
import com.iglusoft.api.exceptions.BusinessException;
import com.iglusoft.api.services.OfferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/offers")
public class OfferController {
    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping()
    public ResponseEntity<List<OfferDto>> getAllOffers() {
        return ResponseEntity.ok(this.offerService.findAllOffers().stream().map(OfferDto::new).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferDto> getOfferById(@PathVariable long id) {
        return ResponseEntity.ok(new OfferDto(this.offerService.findOfferById(id)));
    }

    @PostMapping()
    public ResponseEntity<OfferDto> newOffer(@RequestBody @Valid NewEditOffer newEditOffer) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new OfferDto(this.offerService.saveOffer(newEditOffer)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OfferDto> updateOffer(@PathVariable Long id, @RequestBody @Valid NewEditOffer newEditOffer) {
        if (!Objects.equals(id, newEditOffer.id())) throw new BusinessException("Invalid ID");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new OfferDto(this.offerService.saveOffer(newEditOffer)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteOffer(@PathVariable Long id) {
        this.offerService.deleteOffer(id);
        return ResponseEntity.noContent().build();
    }
}
