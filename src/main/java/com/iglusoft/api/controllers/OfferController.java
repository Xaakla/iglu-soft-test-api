package com.iglusoft.api.controllers;

import com.iglusoft.api.dtos.NewEditOffer;
import com.iglusoft.api.dtos.OfferDto;
import com.iglusoft.api.services.OfferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/offers")
public class OfferController {
    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping()
    public ResponseEntity<OfferDto> newOffer(@RequestBody @Valid NewEditOffer newEditOffer) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new OfferDto(this.offerService.saveOffer(newEditOffer)));
    }
}
