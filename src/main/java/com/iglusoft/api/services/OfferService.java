package com.iglusoft.api.services;

import com.iglusoft.api.database.repositories.OfferRepository;
import org.springframework.stereotype.Service;

@Service
public class OfferService {
    private final OfferRepository offerRepository;

    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }
}
