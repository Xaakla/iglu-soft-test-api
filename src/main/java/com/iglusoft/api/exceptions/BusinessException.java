package com.iglusoft.api.exceptions;

/**
 * A classe {@code BusinessException} é uma exceção de tempo de execução usada para representar erros relacionados a regras de negócio.
 *
 * <p>
 * Esta exceção é lançada quando ocorre uma violação das regras de negócio da aplicação durante a execução.
 * </p>
 *
 * <p>
 * A classe estende {@link RuntimeException}, o que significa que é uma exceção de não verificação, não exigindo a declaração de throws ou try-catch em métodos que a lançam.
 * </p>
 *
 * <p><strong>Exemplo de Uso:</strong></p>
 * <pre>{@code
 * public void validateUser(User user) {
 *     if (user.getName() == null || user.getName().isEmpty()) {
 *         throw new BusinessException("Name cannot be empty.");
 *     }
 *     if (user.getAge() <= 0) {
 *         throw new BusinessException("Age must be greater than zero.");
 *     }
 * }
 * }</pre>
 *
 * <p>
 * Neste exemplo, a exceção {@code BusinessException} é lançada quando ocorrem violações das regras de negócio ao validar um objeto {@code User}.
 * </p>
 */
public class BusinessException extends RuntimeException {

    /**
     * Cria uma nova instância de {@code BusinessException} com a mensagem de erro especificada.
     *
     * @param message A mensagem de erro que descreve a violação das regras de negócio.
     */
    public BusinessException(String message) {
        super(message);
    }
}

