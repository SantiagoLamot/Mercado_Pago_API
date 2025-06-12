package com.example.API_MP.entidades;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequestDTO {
    @NotNull(message = "Debe enviar id del producto a comprar")
    long id;
}
