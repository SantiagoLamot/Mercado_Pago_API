package com.example.API_MP.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.API_MP.entidades.Transacciones;

public interface TransaccionesRepository extends JpaRepository<Transacciones, Long> {
}
