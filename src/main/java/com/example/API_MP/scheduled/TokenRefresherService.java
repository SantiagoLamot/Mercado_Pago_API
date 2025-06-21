package com.example.API_MP.scheduled;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.API_MP.entidades.OauthToken;
import com.example.API_MP.entidades.OauthTokenRequestDTO;
import com.example.API_MP.service.MercadoPagoService;
import com.example.API_MP.service.OauthService;
import com.example.API_MP.util.EncriptadoUtil;

@Service
public class TokenRefresherService {
    private final MercadoPagoService mercadoPagoService;
    private final OauthService oauthService;

    public TokenRefresherService(MercadoPagoService mercadoPagoService, OauthService oauthService){
        this.mercadoPagoService = mercadoPagoService;
        this.oauthService = oauthService;
    }

    @Scheduled(fixedRate = 3600000) // cada 1 hora (en milisegundos)
    public void refrescarTokens() {
        System.out.println("Se ejecuto scheduler token refresh");
        List<OauthToken> tokens = oauthService.obtenerTokenDeUsuariosVendedores();
        for (OauthToken token : tokens) {
            if (tokenExpirado(token)) {
                try {
                    OauthTokenRequestDTO nuevoToken = mercadoPagoService.refrescarToken(EncriptadoUtil.desencriptar(token.getRefreshToken()));
                    oauthService.guardarToken(nuevoToken, token.getUsuario());
                } catch (Exception e) {
                    System.err.println("Error actualizando token para usuario " + token.getUsuario().getNombre());
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    private boolean tokenExpirado(OauthToken token) {
        return token.getExpiresAt().isBefore(LocalDateTime.now().plusMinutes(11)); // margen de seguridad
    }
}