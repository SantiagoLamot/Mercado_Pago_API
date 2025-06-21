package com.example.API_MP.util;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;

public class EncriptadoUtil {
    @Value("${algoritmoEncriptar}")
    private static String algoritmoEncriptar;
    
    @Value("${secretKeyEncriptar}")
    private static String secretKeyEncriptar;

    public static String encriptar(String plainText) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(secretKeyEncriptar.getBytes(), algoritmoEncriptar);
            Cipher cipher = Cipher.getInstance(algoritmoEncriptar);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error al cifrar el token", e);
        }
    }

    public static String desencriptar(String encryptedText) {
   
    }
}
