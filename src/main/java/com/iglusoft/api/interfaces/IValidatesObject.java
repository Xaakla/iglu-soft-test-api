package com.iglusoft.api.interfaces;

import com.iglusoft.api.commons.ObjectValidationResponse;

/**
 * A interface {@code IValidatesObject<T>} define um contrato para a validação de objetos genéricos.
 *
 * @param <T> O tipo genérico dos objetos a serem validados.
 *
 * <p>
 * Esta interface possui um único método:
 * </p>
 * <ul>
 *     <li>{@link #validate(T)}: Um método que recebe um objeto do tipo genérico {@code T} e retorna uma resposta de validação {@link ObjectValidationResponse}.
 *         Este método é responsável por realizar a validação do objeto fornecido e retornar uma resposta indicando se o objeto é válido ou inválido, juntamente com uma mensagem explicativa, se necessário.
 *     </li>
 * </ul>
 *
 * <p><strong>Exemplo de Implementação:</strong></p>
 * <pre>{@code
 * public class UserValidator implements IValidatesObject<User> {
 *
 *     {@literal @}Override
 *     public ObjectValidationResponse validate(User user) {
 *         if (user.getName() == null || user.getName().isEmpty()) {
 *             return new ObjectValidationResponse(false, "Name cannot be empty.");
 *         }
 *         if (user.getAge() <= 0) {
 *             return new ObjectValidationResponse(false, "Age must be greater than zero.");
 *         }
 *         return new ObjectValidationResponse(true, "User is valid.");
 *     }
 * }
 * }</pre>
 *
 * <p>
 * Esta interface permite que diferentes tipos de objetos sejam validados de acordo com as regras específicas definidas pelas implementações da interface.
 * </p>
 */
public interface IValidatesObject<T> {

    /**
     * Realiza a validação do objeto fornecido e retorna uma resposta de validação.
     *
     * @param t O objeto a ser validado.
     * @return Uma {@link ObjectValidationResponse} indicando se o objeto é válido ou inválido, juntamente com uma mensagem explicativa, se necessário.
     */
    ObjectValidationResponse validate(T t);
}

