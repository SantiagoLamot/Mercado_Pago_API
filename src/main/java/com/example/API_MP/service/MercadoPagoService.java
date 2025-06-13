package com.example.API_MP.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.API_MP.entidades.ProductoRequestDTO;
import com.example.API_MP.entidades.Productos;
import com.example.API_MP.entidades.Transacciones;
import com.example.API_MP.entidades.Usuarios;
import com.example.API_MP.entidades.WebhookDTO;
import com.example.API_MP.repository.ProductosRepository;
import com.example.API_MP.repository.TransaccionesRepository;
import com.example.API_MP.repository.UsuariosRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.payment.PaymentRefund;
import com.mercadopago.resources.preference.Preference;

@Service
public class MercadoPagoService {

    @Value("${mercadopago.access-token}")
    String accessToken;

    private final ProductosRepository productoRepository;
    private final TransaccionesRepository transaccionRepository;
    private final UsuariosRepository usuariosRepository;

    public MercadoPagoService(ProductosRepository p, TransaccionesRepository t, UsuariosRepository u) {
        this.productoRepository = p;
        this.transaccionRepository = t;
        this.usuariosRepository = u;
    }

    public String crearPreferencia(ProductoRequestDTO p) throws Exception {
        // Se busca el producto recibido
        Productos producto = productoRepository.findById(p.getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        // Se chequea que no este vendido ni reservado
        if (!producto.estaDisponible()) {
            throw new RuntimeException("Producto vendido o reservado, prueba mas tarde");
        }
        // Inicializa config
        MercadoPagoConfig.setAccessToken(accessToken);

        // Crea el ítem
        PreferenceItemRequest item = PreferenceItemRequest.builder()
                .title(producto.getNombre())
                .quantity(1)
                .currencyId("ARS")
                .unitPrice(new BigDecimal(producto.getPrecio()))
                .build();

        // ACA CAMBIAR y Obtener el usuario logueado el cual va a comprar
        Usuarios usuarioComprador = usuariosRepository.findById(new Long("1"))
                .orElseThrow(() -> new RuntimeException("usuario no encontrado"));

        // Se crea la transaccion en la base de datos y se obtiene la misma guardada con
        // su id
        Transacciones transaccion = new Transacciones("Pendiente", usuarioComprador, producto);
        Transacciones transaccionSave = transaccionRepository.save(transaccion);

        // Arma la preferencia
        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(List.of(item))
                .externalReference(transaccionSave.getId().toString()) // Aca se manda el id de la transaccion para
                                                                       // obtenerlo cuando se haga el pago
                .build();

        // Se marca el producto como reservado
        producto.setReservado(true);
        producto.setFecha_reserva(LocalDateTime.now());
        productoRepository.save(producto);

        // Se termina la preferencia
        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        // Retorna la URL de pago
        return preference.getInitPoint();
    }

    public void procesarWebhook(WebhookDTO webhook) {
        if (!"payment".equalsIgnoreCase(webhook.getType())) {
            System.out.println("Webhook ignorado: tipo no soportado " + webhook.getType());
            return;
        }

        try {
            // Obtengo el ID de pago
            String paymentId = webhook.getData().getId();

            // Con el id obtenido busco el pago
            PaymentClient client = new PaymentClient();
            Payment payment = client.get(Long.parseLong(paymentId));

            // obtengo el stado del pafo
            String estado = payment.getStatus();

            // Obtengo el ID del la transaccion para cambiuarte el estado
            String externalReference = payment.getExternalReference();

            if (externalReference == null) {
                System.out.println("No se encontró externalReference (transactionId)");
                return;
            }

            // Se castea a Long se va a buscar la transaccion
            Long transactionId = Long.parseLong(externalReference);
            Transacciones transaccion = transaccionRepository.findById(transactionId)
                    .orElseThrow(() -> new RuntimeException("Transacción no encontrada: ID " + transactionId));
            // Se chequea que no haya sido vendido anteriormente
            Productos producto = transaccion.getProducto();
            if (producto.getVendido()) {
                // en caso que se haya venido se reembolsa
                System.out.println("Producto ya no está disponible, haciendo reembolso...");
                reembolsarPago(paymentId);
                transaccion.setEstado("reembolsado");
                return;
            }

            // se setean los estados en caso que el producto este disponible
            producto.setVendido(true);
            transaccion.setEstado("Pago");
            productoRepository.save(producto);
            transaccionRepository.save(transaccion);
        } catch (Exception e) {
            System.out.println("Error al procesar webhook: " + e.getMessage());
        }
    }

    private void reembolsarPago(String paymentId) {
        try {
            MercadoPagoConfig.setAccessToken(accessToken);
            System.out.println("Procesando reembolso para ID de pago: " + paymentId);
            // Obtener el pago con PaymentClient
            PaymentClient client = new PaymentClient();
            Payment payment = client.get(Long.parseLong(paymentId));
            System.out.println("Estado del pago: " + payment.getStatus());
            System.out.println("ExternalReference: " + payment.getExternalReference());
            // Ejecutar el reembolso
            PaymentRefund refundPayment = client.refund(payment.getId());

            System.out.println("Reembolso exitoso: " + refundPayment.getId());

        } catch (MPApiException e) {
            System.out.println("Error MercadoPago: " + e.getApiResponse().getContent());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }
}