package com.example.API_MP.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.API_MP.entidades.ProductoRequestDTO;
import com.example.API_MP.entidades.WebhookDTO;
import com.example.API_MP.service.MercadoPagoService;

@RestController
@RequestMapping("/api")
public class PagoController {
    private final MercadoPagoService mercadoPagoService;

    public PagoController(MercadoPagoService mercadoPagoService) {
        this.mercadoPagoService = mercadoPagoService;
    }

    @PostMapping("/pagos/crear")
    public ResponseEntity<?> crearPago(@RequestBody ProductoRequestDTO request) {
        try {
            String initPoint = mercadoPagoService.crearPreferencia(request);
            return ResponseEntity.ok(initPoint);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear preferencia: " + e.getMessage());
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> recibirWebhook(@RequestBody WebhookDTO webhook) {
        try {
            mercadoPagoService.procesarWebhook(webhook);
            return ResponseEntity.ok("Webhook procesado");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Error procesando webhook: " + e.getMessage());
        }
    }
}