package com.example.API_MP.entidades;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "oauth_tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OauthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accessToken;
    private String refreshToken;
    private String publicKey;
    private Long userId; // ID del vendedor en Mercado Pago
    private LocalDateTime expiresAt;
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuarios usuario;

    private boolean liveMode;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}