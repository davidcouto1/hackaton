package org.example.controller;

import org.example.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ExceptionHandlerAdvice {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("erro", ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler({AuthenticationException.class, AccessDeniedException.class})
    public ResponseEntity<?> handleAuthException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("erro", "Não autenticado ou acesso negado");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler({NoSuchElementException.class, org.example.exception.NotFoundException.class})
    public ResponseEntity<?> handleNotFoundException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("erro", "Recurso não encontrado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Exemplo para RateLimitException (se existir)
    @ExceptionHandler(org.example.exception.RateLimitException.class)
    public ResponseEntity<?> handleRateLimitException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("erro", "Limite de requisições excedido");
        return ResponseEntity.status(429).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        logger.error("Erro interno não tratado", ex);
        Map<String, String> error = new HashMap<>();
        error.put("erro", "Erro interno do servidor");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
