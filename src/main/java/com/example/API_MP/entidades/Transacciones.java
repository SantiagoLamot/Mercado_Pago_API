package com.example.API_MP.entidades;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transacciones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String estado;

    private LocalDateTime fecha = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "usuario_comprador_id")
    private Usuarios usuarioComprador;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Productos producto;

    public Transacciones(String estado, Usuarios usuarioComprador, Productos producto){
        this.estado = estado;
        this.fecha = LocalDateTime.now();
        this.usuarioComprador = usuarioComprador;
        this.producto = producto;
    }
}