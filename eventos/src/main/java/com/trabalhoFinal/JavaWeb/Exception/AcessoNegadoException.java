package com.trabalhoFinal.JavaWeb.Exception;

public class AcessoNegadoException extends RuntimeException{
    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
