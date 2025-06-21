package com.example.API_MP.util;
import org.springframework.beans.factory.annotation.Value;

public class EncriptadoUtil {
    @Value("${algoritmoEncriptar}")
    private static String algoritmoEncriptar;
    
    @Value("${secretKeyEncriptar}")
    private static String secretKeyEncriptar;

    public static String encriptar(String plainText) {
    }

    public static String desencriptar(String encryptedText) {
    }
}
