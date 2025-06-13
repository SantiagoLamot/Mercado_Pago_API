package com.example.API_MP.scheduled;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.API_MP.entidades.Productos;
import com.example.API_MP.repository.ProductosRepository;

@Component
public class ProductoScheduler {

    private final ProductosRepository productoRepository;

    public ProductoScheduler(ProductosRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Scheduled(fixedRate = 60000) // Ejecuta cada 1 minuto
    public void liberarProductosReservados() {
        System.out.println("Se ejecuto scheduler :)");
        List<Productos> productos = productoRepository.findByReservadoTrue();

        for (Productos p : productos) {
            if (p.getFecha_reserva().isBefore(LocalDateTime.now().minusMinutes(2))) {
                p.setReservado(false);
                productoRepository.save(p);
            }
        }
    }
}
