package com.example.API_MP.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.API_MP.entidades.OauthTokenRequestDTO;
import com.example.API_MP.entidades.ProductoRequestDTO;
import com.example.API_MP.entidades.Productos;
import com.example.API_MP.entidades.Transacciones;
import com.example.API_MP.entidades.Usuarios;
import com.example.API_MP.entidades.WebhookDTO;
import com.example.API_MP.excepciones.TokenRevocadoException;
import com.example.API_MP.repository.ProductosRepository;
import com.example.API_MP.repository.TransaccionesRepository;
import com.example.API_MP.repository.UsuariosRepository;
import com.example.API_MP.util.EncriptadoUtil;
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

    // @Value("${mercadopago.access-token}")
    // String accessToken;
    @Value("${clientId}")
    String clientId;

    @Value("${clientSecret}")
    String clientSecret;

    private final ProductosRepository productoRepository;
    private final TransaccionesRepository transaccionRepository;
    private final UsuariosRepository usuariosRepository;
    private final OauthService oauthService;
    private final EncriptadoUtil encriptadoUtil;

    public MercadoPagoService(ProductosRepository p, TransaccionesRepository t, UsuariosRepository u,
            OauthService o, EncriptadoUtil e) {
        this.productoRepository = p;
        this.transaccionRepository = t;
        this.usuariosRepository = u;
        this.oauthService = o;
        this.encriptadoUtil = e;
    }

    // ===============CREAR PREFERENCIA===============

    public String crearPreferencia(ProductoRequestDTO p) throws Exception {
        // Se busca el producto recibido
        Productos producto = productoRepository.findById(p.getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        // Se chequea que no este vendido ni reservado
        if (!producto.estaDisponible()) {
            throw new RuntimeException("Producto vendido o reservado, prueba mas tarde");
        }
        // me falta obtener el id del vendedor ej: producto.getVendedor.getId() = 1
        String accessTokenEncriptado = oauthService.obtenerAccessTokenPorId(1L);
        String accessToken = encriptadoUtil.desencriptar(accessTokenEncriptado);

        // Verifico que no este vencido ni revocado
        if (!oauthService.AccessTokenValido(accessToken)) {
            throw new RuntimeException("Access token vencido o revocado por el vendedor");
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
        Usuarios usuarioComprador = usuariosRepository.findById(new Long("2"))
                .orElseThrow(() -> new RuntimeException("usuario no encontrado"));

        // Se crea la transaccion en la base de datos y se obtiene id de la misma
        Transacciones transaccion = new Transacciones("Pendiente", usuarioComprador, producto);
        Transacciones transaccionSave = transaccionRepository.save(transaccion);

        // Tiempo actual
        OffsetDateTime now = OffsetDateTime.now();

        // Tiempo de expiración: 2 minutos desde ahora
        OffsetDateTime expirationFrom = now;
        OffsetDateTime expirationTo = now.plusMinutes(2);

        // Arma la preferencia
        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(List.of(item))
                // Aca se manda el id de la transaccion para obtenerlo cuando se haga el pago
                .externalReference(transaccionSave.getId().toString())
                // Aca se setean datos para que la URL expire y no sea comprada mas alla de lo
                // que dura la reserva
                .expires(true)
                .expirationDateFrom(expirationFrom)
                .expirationDateTo(expirationTo)
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

    // =============== MANEJAR WEBHOOK ===============

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

            // Obtengo el estado de la transaccion
            String estado = payment.getStatus();

            // Obtengo el ID del la transaccion y luego la transaccion completa
            String externalReference = payment.getExternalReference();
            Long transactionId = Long.parseLong(externalReference);
            Transacciones transaccion = transaccionRepository.findById(transactionId)
                    .orElseThrow(() -> new RuntimeException("Transacción no encontrada: ID " + transactionId));

            // Obtengo el producto de la transaccion
            Productos producto = transaccion.getProducto();

            // Obtengo el accessToken del vendedor por si hay que rembolsar
            String accessTokenEncriptado = oauthService.obtenerAccessTokenPorId(1L);
            String accessToken = encriptadoUtil.desencriptar(accessTokenEncriptado);


            // Se verifica que se encontro el id de Transaccion
            if (externalReference == null) {
                System.out.println("No se encontró externalReference (transactionId)");
                reembolsarPago(paymentId, accessToken);
                return;
            }
            // Se chequea que no haya sido vendido anteriormente
            if (producto.getVendido()) {
                // en caso que se haya venido se reembolsa
                System.out.println("Producto ya no está disponible, haciendo reembolso...");
                reembolsarPago(paymentId, accessToken);
                transaccion.setEstado("reembolsado");
                return;
            }
            
            //FALTA ARREGAR PARA REEMBOLSAR SI EL MONTO DEL PROD SE MODIFICO
            // if (payment.getTransactionAmount() !=
            // BigDecimal.valueOf(producto.getPrecio())) {
            // System.out.println("El producto cambio el precio.");
            // reembolsarPago(paymentId);
            // return;
            // }

            // se setean los estados en caso que pase las validaciones
            if ("approved".equalsIgnoreCase(estado)) {
                producto.setVendido(true);
                transaccion.setEstado("Pago");
                productoRepository.save(producto);
                transaccionRepository.save(transaccion);
            }
        } catch (Exception e) {
            System.out.println("Error al procesar webhook: " + e.getMessage());
        }
    }

    private void reembolsarPago(String paymentId, String accessToken) {
        try {
            MercadoPagoConfig.setAccessToken(accessToken);
            // Obtener el pago con PaymentClient
            PaymentClient client = new PaymentClient();
            Payment payment = client.get(Long.parseLong(paymentId));
            // Ejecutar el reembolso
            PaymentRefund refundPayment = client.refund(payment.getId());

            System.out.println("Reembolso exitoso: " + refundPayment.getId());

        } catch (MPApiException e) {
            System.out.println("Error MercadoPago: " + e.getApiResponse().getContent());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    public OauthTokenRequestDTO refrescarToken(String refreshToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String url = "https://api.mercadopago.com/oauth/token";

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "refresh_token");
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("refresh_token", refreshToken);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<OauthTokenRequestDTO> response = restTemplate.postForEntity(url, request, OauthTokenRequestDTO.class);

            return response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST || e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new TokenRevocadoException("El refresh token fue revocado o no es válido");
            }
            throw e;
        }
    }
}