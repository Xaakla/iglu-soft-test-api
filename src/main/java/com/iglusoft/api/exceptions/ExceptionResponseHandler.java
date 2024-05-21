package com.iglusoft.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * A classe {@code ExceptionResponseHandler} é uma classe anotada com {@code @RestControllerAdvice} responsável por lidar com exceções específicas lançadas durante a execução da aplicação e retornar respostas HTTP apropriadas.
 *
 * <p>
 * Esta classe define métodos anotados com {@code @ExceptionHandler} para tratar exceções de negócio ({@link BusinessException}) e exceções de não encontrados ({@link NotFoundException}).
 * </p>
 *
 * <p>
 * Cada método de tratamento de exceção retorna uma resposta HTTP adequada com base no tipo de exceção capturada.
 * </p>
 */
@RestControllerAdvice
public class ExceptionResponseHandler {

    /**
     * Trata exceções de negócio ({@code BusinessException}) lançadas durante a execução da aplicação.
     * Retorna uma resposta HTTP 400 Bad Request contendo a mensagem de erro da exceção.
     *
     * @param e A exceção de negócio capturada.
     * @return Uma resposta HTTP 400 Bad Request com a mensagem de erro da exceção.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BusinessException.class)
    ResponseEntity<Object> catchBusinessException(BusinessException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    /**
     * Trata exceções de não encontrados ({@code NotFoundException}) lançadas durante a execução da aplicação.
     * Retorna uma resposta HTTP 404 Not Found.
     *
     * @param e A exceção de não encontrados capturada.
     * @return Uma resposta HTTP 404 Not Found.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<Object> catchNotFoundException(NotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}

