package com.example.API_MP.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.API_MP.entidades.Productos;

public interface ProductosRepository extends JpaRepository<Productos, Long> {
    public List<Productos> findByReservadoTrue();
}