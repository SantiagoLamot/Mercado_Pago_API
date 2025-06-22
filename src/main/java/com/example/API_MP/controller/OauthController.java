package com.example.API_MP.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.API_MP.service.OauthService;

@RestController
@RequestMapping("/oauth")
public class OauthController {
    OauthService oauthService;

    public OauthController(OauthService os) {
        this.oauthService = os;
    }

    @GetMapping("/init")
    public ResponseEntity<String> init() {
        try{
            return ResponseEntity.ok(oauthService.UrlAutorizacion());
        }
        catch(Exception e){
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Error al crear URL de autenticación: " + e.getMessage());
        }
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam("code") String code, @RequestParam("state") String state) {
        try {
            oauthService.obtenerAccessToken(code, state);
            return ResponseEntity.ok("Autenticación con Mercado Pago completada correctamente.");
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error al completar la autenticación: " + e.getMessage());
        }
    }

}