package com.example.API_MP.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.API_MP.entidades.Usuarios;

public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {
    Optional<Usuarios> findByEmail(String email);
}