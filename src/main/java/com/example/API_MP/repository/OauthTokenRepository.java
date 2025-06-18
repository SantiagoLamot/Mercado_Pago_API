package com.example.API_MP.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.API_MP.entidades.OauthToken;


public interface OauthTokenRepository extends JpaRepository<OauthToken, Long>{
    Optional<OauthToken> findByUsuarioId(Long userId);
}
