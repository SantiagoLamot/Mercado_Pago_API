package com.example.API_MP.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.API_MP.entidades.Productos;

public interface ProductosRepository extends JpaRepository<Productos, Long> {
}