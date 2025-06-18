package com.example.API_MP.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.API_MP.entidades.OauthToken;
import com.example.API_MP.entidades.OauthTokenRequestDTO;
import com.example.API_MP.entidades.Usuarios;
import com.example.API_MP.repository.OauthTokenRepository;

@Service
public class OauthService {

    @Value("${clientId}")
    String clientId;

    @Value("${redirectUrl}")
    String redirectUrl;

    @Value("${clientSecret}")
    String clientSecret;

    private final UsuariosService usuariosService;
    private final OauthTokenRepository oauthRepository;
    private final StateOauthService stateOauthService;

    public OauthService(UsuariosService usuariosService, OauthTokenRepository oauthRepository, StateOauthService stateOauthService) {
        this.usuariosService = usuariosService;
        this.oauthRepository = oauthRepository;
        this.stateOauthService = stateOauthService;
    }

    public String UrlAutorizacion() {
        Long idUsuarioLogueado = usuariosService.obtenerUsuariosPorId(1L)// ACA OBTENER EL ID DEL USUARIO LOGUEADO
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
        String state = UUID.randomUUID().toString();
        stateOauthService.guardarStateOauth(idUsuarioLogueado, state);
        return "https://auth.mercadopago.com.ar/authorization?response_type=code" +
                "&client_id=" + clientId +
                "&redirect_uri=" + redirectUrl +
                "&state=" + state;
    }

    public String obtenerAccessToken(String code, String state) {
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
        ResponseEntity<OauthTokenRequestDTO> response = restTemplate.postForEntity(
                "https://api.mercadopago.com/oauth/token",
                request,
                OauthTokenRequestDTO.class);

        guardarToken(response.getBody(), usuariosService.obtenerUsuarioPorState(state));

        return response.toString();
    }

    public void guardarToken(OauthTokenRequestDTO oauthTokenDTO, Usuarios usuario) {
        OauthToken token = new OauthToken();
        token.setAccessToken(oauthTokenDTO.getAccessToken());
        token.setRefreshToken(oauthTokenDTO.getRefreshToken());
        token.setPublicKey(oauthTokenDTO.getPublicKey());
        token.setUserId(oauthTokenDTO.getUserId());
        token.setLiveMode(oauthTokenDTO.isLiveMode());
        token.setExpiresAt(LocalDateTime.now().plusSeconds(oauthTokenDTO.getExpiresIn()));
        token.setUsuario(usuario);

        oauthRepository.save(token);
    }

    public String obtenerAccessTokenPorId(Long id){
        return oauthRepository.findByUsuarioId(id)
            .orElseThrow(()-> new RuntimeException("no se encontro access token"))
            .getAccessToken();
    }
}
