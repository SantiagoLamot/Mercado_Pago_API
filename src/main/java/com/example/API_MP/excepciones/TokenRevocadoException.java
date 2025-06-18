package com.example.API_MP.excepciones;

public class TokenRevocadoException extends RuntimeException {
    public TokenRevocadoException(String mensaje) {
        super(mensaje);
    }
}
