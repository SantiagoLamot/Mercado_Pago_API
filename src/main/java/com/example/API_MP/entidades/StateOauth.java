package com.example.API_MP.entidades;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class StateOauth {
    @NotNull
    @Column(name= "id_usuario")
    Long idUsuario;
    @NotNull
    String state;
    LocalDateTime creado = LocalDateTime.now();
    public StateOauth(Long id, String state){
        this.idUsuario = id;
        this.state = state;
        this.creado = LocalDateTime.now();
    }
}
