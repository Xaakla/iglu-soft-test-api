package com.iglusoft.api.commons;

/**
 * O record {@code ObjectValidationResponse} representa uma resposta de validação de objeto com um resultado booleano e uma mensagem associada.
 *
 * <p>
 * Este record é usado para encapsular o resultado de uma operação de validação de objeto, indicando se o objeto é válido ou inválido, juntamente com uma mensagem explicativa, se necessário.
 * </p>
 */
public record ObjectValidationResponse(boolean result, String message) {

    /**
     * Verifica se a resposta de validação indica que o objeto é válido.
     *
     * @return {@code true} se o objeto for válido, {@code false} caso contrário.
     */
    public boolean isValid() {
        return result;
    }

    /**
     * Verifica se a resposta de validação indica que o objeto é inválido.
     *
     * @return {@code true} se o objeto for inválido, {@code false} caso contrário.
     */
    public boolean isInvalid() {
        return !result;
    }
}

