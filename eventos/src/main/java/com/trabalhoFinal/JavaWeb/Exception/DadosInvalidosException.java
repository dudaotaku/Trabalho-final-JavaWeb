package com.trabalhoFinal.JavaWeb.Exception;

public class DadosInvalidosException extends RuntimeException{
    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
