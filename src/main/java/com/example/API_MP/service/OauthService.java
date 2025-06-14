package com.example.API_MP.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OauthService {
    @Value("${clientId}")
    String clientId;
    
    @Value("${redirectUrl}")
    String redirectUrl;


    public String UrlAutorizacion() {
        return "https://auth.mercadopago.com.ar/authorization?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirectUrl;

    }
    
}
