package com.example.scrapetok.infraestructure;


import com.example.scrapetok.application.PaymentService;
import com.example.scrapetok.application.mercadoservice.CheckoutProService;
import com.example.scrapetok.config.ExternalReference;
import com.example.scrapetok.domain.DTO.CheckoutProRequest;
import com.example.scrapetok.domain.DTO.CheckoutProResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadopago.resources.payment.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/mercadoPago")
public class CheckoutProController {
    @Autowired
    private CheckoutProService service;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static final Logger log = LoggerFactory.getLogger(CheckoutProController.class);


    @Value("${frontend.compra.asesoria.success-url}")
    private String frontendSuccessUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Crear preferencia de pago
     */
    @PostMapping("/create-preference")
    public ResponseEntity<CheckoutProResponse> createPreference(@RequestBody CheckoutProRequest request) {
        try {
            CheckoutProResponse response = service.createPreference(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Webhook para notificaciones de MercadoPago
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody Map<String, Object> payload) {
        try {
            System.out.println("Webhook recibido: " + payload);

            // Verificar si es notificación de pago
            if ("payment".equals(payload.get("type"))) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                Long paymentId = Long.parseLong(data.get("id").toString());

                // Obtener información actualizada del pago
                Payment payment = service.getPayment(paymentId);
                processPayment(payment);
            }

            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Error procesando webhook: " + e.getMessage());
            return ResponseEntity.status(500).body("Error");
        }
    }

    /**
     * Procesar el pago según su estado
     */
    private void processPayment(Payment payment) {
        String status = payment.getStatus();
        String externalReference = payment.getExternalReference();

        switch (status) {
            case "approved":
                // Pago aprobado - actualizar tu base de datos
                // Aquí actualizarías el estado del pedido en tu BD
                break;

            case "rejected":
                // Pago rechazado
                break;

            case "pending":
                // Pago pendiente
                break;

            case "cancelled":
                // Pago cancelado
                break;

            default:
                // Estado desconocido
                break;
        }
    }

    /**
     * Endpoint para obtener información de un pago (opcional)
     */
    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long paymentId) {
        try {
            Payment payment = service.getPayment(paymentId);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Página de éxito
     */
    @GetMapping("/success")
    public ResponseEntity<String> success(@RequestParam Map<String, String> params) {
        String externalReference = params.get("external_reference");

        Long userId = null;
        String tier = null;

        if (Objects.nonNull(externalReference) && !externalReference.isEmpty()) {
            try {
                String decodedReference = URLDecoder.decode(externalReference, StandardCharsets.UTF_8);

                // Suponiendo que tu ExternalReference ahora contiene userId y tier
                ExternalReference reference = objectMapper.readValue(decodedReference, ExternalReference.class);
                userId = reference.getUserId();
                tier = reference.getTier();
            } catch (Exception e) {
                System.err.println("❌ Error parseando external_reference: " + externalReference);
                e.printStackTrace();
                return ResponseEntity.badRequest().body("Error procesando el pago: " + e.getMessage());
            }
        }

        if (Objects.isNull(userId) || Objects.isNull(tier)) {
            System.err.println("❌ No se pudo extraer userId o tier del external_reference: " + externalReference);
            return ResponseEntity.badRequest().body("Error procesando el pago: userId o tier no encontrados.");
        }

        try {
            paymentService.activarSuscripcion(userId, params.get("payment_id"), tier);
            System.out.println("✅ Suscripción activada correctamente para el usuario: " + userId);
        } catch (Exception e) {
            System.err.println("❌ Error activando suscripción: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error activando suscripción: " + e.getMessage());
        }

        // Redirigir a la URL de éxito del frontend
        try {
            URI redirectUri = new URI(frontendSuccessUrl);
            return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
        } catch (Exception e) {
            System.err.println("❌ Error creando URI de redirección: " + e.getMessage());
            return ResponseEntity.ok("Pago exitoso! ID: " + params.get("payment_id"));
        }
    }

    /**
     * Página de fallo
     */
    @GetMapping("/failure")
    public ResponseEntity<String> failure(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok("Pago falló. Intenta nuevamente.");
    }

    /**
     * Página de pendiente
     */
    @GetMapping("/pending")
    public ResponseEntity<String> pending(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok("Pago pendiente de confirmación.");
    }
}