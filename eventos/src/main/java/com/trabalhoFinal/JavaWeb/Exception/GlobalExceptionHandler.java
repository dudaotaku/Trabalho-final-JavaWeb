package com.trabalhoFinal.JavaWeb.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

    @ExceptionHandler(RegistroNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleRegistroNaoEncontrado(RegistroNaoEncontradoException e) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(EventoSemVagasException.class)
    public ResponseEntity<Map<String, String>> handleEventoSemVagas(EventoSemVagasException e) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(DadosInvalidosException.class)
    public ResponseEntity<Map<String, String>> handleDadosInvalidos(DadosInvalidosException e) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> erros = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                erros.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAcessoNegado(AccessDeniedException e) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", "Acesso negado. Você não tem permissão para acessar este recurso.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }
}
