package com.example.API_MP.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class OauthService {
    @Value("${clientId}")
    String clientId;
    
    @Value("${redirectUrl}")
    String redirectUrl;

    @Value("${clientSecret}")
    String clientSecret;


    public String UrlAutorizacion() {
        return "https://auth.mercadopago.com.ar/authorization?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirectUrl;

    }


    public String obtenerAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);
        body.add("redirect_uri", redirectUrl);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.mercadopago.com/oauth/token",
                request,
                String.class
        );

        // 👇 Acá podrías parsear el JSON para extraer y guardar access_token, user_id, etc.
        System.out.println("🔐 Respuesta de Mercado Pago:");
        System.out.println(response.getBody());

        return response.getBody(); // en producción, parsear y guardar en DB
    }
    
}
