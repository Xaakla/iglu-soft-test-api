package com.iglusoft.api.exceptions;

/**
 * A classe {@code NotFoundException} é uma exceção de tempo de execução usada para representar casos em que um recurso não pôde ser encontrado.
 *
 * <p>
 * Esta exceção é lançada quando uma solicitação para um recurso específico não pode ser atendida porque o recurso não existe ou não foi encontrado.
 * </p>
 *
 * <p>
 * A classe estende {@link RuntimeException}, o que significa que é uma exceção de não verificação, não exigindo a declaração de throws ou try-catch em métodos que a lançam.
 * </p>
 *
 * <p><strong>Exemplo de Uso:</strong></p>
 * <pre>{@code
 * public User getUserById(Long userId) {
 *     User user = userRepository.findById(userId);
 *     if (user == null) {
 *         throw new NotFoundException();
 *     }
 *     return user;
 * }
 * }</pre>
 *
 * <p>
 * Neste exemplo, a exceção {@code NotFoundException} é lançada quando um usuário com o ID fornecido não pode ser encontrado na base de dados.
 * </p>
 */
public class NotFoundException extends RuntimeException {

    /**
     * Cria uma nova instância de {@code NotFoundException} sem uma mensagem específica.
     */
    public NotFoundException() {}

}

