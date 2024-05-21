package com.iglusoft.api.services;

import com.iglusoft.api.database.entities.Offer;
import com.iglusoft.api.database.entities.OfferIngredientMinQuantity;
import com.iglusoft.api.database.repositories.IngredientRepository;
import com.iglusoft.api.database.repositories.OfferIngredientMinQuantityRepository;
import com.iglusoft.api.database.repositories.OfferRepository;
import com.iglusoft.api.dtos.DishOrderDto;
import com.iglusoft.api.dtos.NewEditOffer;
import com.iglusoft.api.dtos.NewEditOfferIngredientMinQuantityDto;
import com.iglusoft.api.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfferService {
    private final OfferRepository offerRepository;
    private final OfferIngredientMinQuantityRepository offerIngredientMinQuantityRepository;
    private final IngredientService ingredientService;

    public OfferService(
            OfferRepository offerRepository,
            OfferIngredientMinQuantityRepository offerIngredientMinQuantityRepository,
            IngredientService ingredientService) {
        this.offerRepository = offerRepository;
        this.offerIngredientMinQuantityRepository = offerIngredientMinQuantityRepository;
        this.ingredientService = ingredientService;
    }

    @Transactional
    public Offer saveOffer(NewEditOffer newEditOffer) {
        boolean isEdit = newEditOffer.id() != null;

        var offerToSave = isEdit ?
                offerRepository.findById(newEditOffer.id())
                        .orElseThrow(NotFoundException::new) : new Offer();

        offerToSave.setName(newEditOffer.name());
        offerToSave.setDiscountAmount(newEditOffer.discountAmount());
        offerToSave.setDiscountType(newEditOffer.discountType());

        if (isEdit)
            offerIngredientMinQuantityRepository.deleteAllByOfferId(offerToSave.getId());

        populateOfferIngredientList(offerToSave, offerToSave.getRequiredIngredients(), newEditOffer.requiredIngredients());
        populateOfferIngredientList(offerToSave, offerToSave.getExcludedIngredients(), newEditOffer.excludedIngredients());

        return offerRepository.save(offerToSave);
    }

    public List<Offer> findAllOffers() {
        return offerRepository.findAll();
    }

    public Offer findOfferById(long id) {
        return offerRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public void deleteOffer(Long id) {
        if (!offerRepository.existsById(id))
            throw new NotFoundException();

        offerIngredientMinQuantityRepository.deleteAllByOfferId(id);
        offerRepository.deleteById(id);
    }

    /**
     * Popula a lista de ingredientes mínimos de uma oferta com base nos dados fornecidos.
     *
     * @param offer          O objeto {@link Offer} associado aos ingredientes mínimos.
     * @param listToPopulate A lista de {@link OfferIngredientMinQuantity} que será populada.
     * @param data           A lista de {@link NewEditOfferIngredientMinQuantityDto} contendo os dados
     *                       para popular a lista.
     *
     * <p>
     * Este método realiza as seguintes operações:
     * </p>
     * <ul>
     *     <li>Limpa a lista de ingredientes mínimos fornecida.</li>
     *     <li>Converte cada objeto {@link NewEditOfferIngredientMinQuantityDto} da lista de dados em um
     *     objeto {@link OfferIngredientMinQuantity} associado à oferta fornecida.</li>
     *     <li>Adiciona os objetos convertidos à lista de ingredientes mínimos.</li>
     * </ul>
     *
     * <p><strong>Exemplo de uso:</strong></p>
     * <pre>{@code
     * Offer offer = new Offer();
     * List<OfferIngredientMinQuantity> ingredientList = new ArrayList<>();
     * List<NewEditOfferIngredientMinQuantityDto> dataList = Arrays.asList(
     *     new NewEditOfferIngredientMinQuantityDto(1L, 5),
     *     new NewEditOfferIngredientMinQuantityDto(2L, 10)
     * );
     *
     * populateOfferIngredientList(offer, ingredientList, dataList);
     * System.out.println("Ingredient List Size: " + ingredientList.size());  // Output: Ingredient List Size: 2
     * }</pre>
     *
     * <p><strong>Decisões de Design:</strong></p>
     * <ul>
     *     <li>Uso de streams para simplificar a conversão e adição dos objetos na lista.</li>
     *     <li>Limpeza da lista de ingredientes mínimos antes de populá-la para garantir que não haja dados antigos.</li>
     * </ul>
     */
    private void populateOfferIngredientList(Offer offer, List<OfferIngredientMinQuantity> listToPopulate, List<NewEditOfferIngredientMinQuantityDto> data) {
        listToPopulate.clear();
        listToPopulate.addAll(data.stream().map(it -> getOfferIngredientMinQuantity(it, offer)).toList());
    }


    /**
     * Converte um objeto {@link NewEditOfferIngredientMinQuantityDto} em um objeto {@link OfferIngredientMinQuantity}
     * associado a uma oferta específica.
     *
     * @param it           O objeto {@link NewEditOfferIngredientMinQuantityDto} contendo os dados de quantidade mínima e paga
     *                     de um ingrediente.
     * @param offerToSave  O objeto {@link Offer} ao qual o ingrediente mínimo será associado.
     * @return Um objeto {@link OfferIngredientMinQuantity} que representa a quantidade mínima e paga de um ingrediente para
     *         uma oferta específica.
     *
     * <p>
     * Este método realiza as seguintes operações:
     * </p>
     * <ul>
     *     <li>Busca o ingrediente requerido pelo ID usando o serviço {@code ingredientService}.</li>
     *     <li>Cria um novo objeto {@link OfferIngredientMinQuantity} e define suas propriedades com base nos dados fornecidos.</li>
     *     <li>Associa o ingrediente e a oferta ao objeto {@link OfferIngredientMinQuantity} criado.</li>
     * </ul>
     *
     * <p><strong>Exemplo de uso:</strong></p>
     * <pre>{@code
     * NewEditOfferIngredientMinQuantityDto dto = new NewEditOfferIngredientMinQuantityDto();
     * dto.setIngredientId(1L);
     * dto.setMinQuantity(5);
     * dto.setPaidQuantity(3);
     *
     * Offer offer = new Offer();
     * OfferIngredientMinQuantity offerIngredientMinQuantity = getOfferIngredientMinQuantity(dto, offer);
     *
     * System.out.println("Ingredient ID: " + offerIngredientMinQuantity.getIngredient().getId());
     * System.out.println("Min Quantity: " + offerIngredientMinQuantity.getMinQuantity());
     * System.out.println("Paid Quantity: " + offerIngredientMinQuantity.getPaidQuantity());
     * }</pre>
     *
     * <p><strong>Decisões de Design:</strong></p>
     * <ul>
     *     <li>Uso do serviço {@code ingredientService} para garantir que o ingrediente existe e está corretamente carregado.</li>
     *     <li>Criação e associação do objeto {@link OfferIngredientMinQuantity} dentro do método para encapsular a lógica de conversão.</li>
     * </ul>
     */
    private OfferIngredientMinQuantity getOfferIngredientMinQuantity(NewEditOfferIngredientMinQuantityDto it, Offer offerToSave) {
        var requiredIngredient = ingredientService.findById(it.getIngredientId());
        var offerIngredientMinQuantity = new OfferIngredientMinQuantity();

        offerIngredientMinQuantity.setMinQuantity(it.getMinQuantity());
        offerIngredientMinQuantity.setPaidQuantity(it.getPaidQuantity());
        offerIngredientMinQuantity.setIngredient(requiredIngredient);
        offerIngredientMinQuantity.setOffer(offerToSave);

        return offerIngredientMinQuantity;
    }

}
