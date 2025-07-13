package com.example.scrapetok.application.mercadoservice;

import com.example.scrapetok.domain.DTO.CheckoutItem;
import com.example.scrapetok.domain.DTO.CheckoutProRequest;
import com.example.scrapetok.domain.DTO.CheckoutProResponse;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CheckoutProService {
    private final PreferenceClient preferenceClient = new PreferenceClient();
    private final PaymentClient paymentClient = new PaymentClient();
    private final MercadoPagoInitializer config;
    private static final Logger log = LoggerFactory.getLogger(CheckoutProService.class);

    public CheckoutProService(MercadoPagoInitializer config) {
        this.config = config;
    }

    public CheckoutProResponse createPreference(CheckoutProRequest request) {
        try {
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(List.of(buildItem(request.getItem())))
                    .backUrls(
                            PreferenceBackUrlsRequest.builder()
                                    .success(config.getSuccessUrl())
                                    .failure(config.getFailureUrl())
                                    .pending(config.getPendingUrl())
                                    .build()
                    )
                    .autoReturn("approved")
                    .externalReference(request.getExternalReference())
                    .notificationUrl(config.getWebhookUrl())
                    .build();

            Preference preference = preferenceClient.create(preferenceRequest);

            return CheckoutProResponse.builder()
                    .preferenceId(preference.getId())
                    .initPoint(preference.getInitPoint())
                    .build();
        } catch (MPApiException e) {
            log.error("❌ ERROR DE API - Status: " + e.getStatusCode());
            log.error("❌ ERROR DE API - Message: " + e.getMessage());
            log.error("❌ ERROR DE API - Response: " + e.getApiResponse());

            if (e.getApiResponse() != null) {
                log.error("❌ ERROR DE API - Response Content: " + e.getApiResponse().getContent());
                log.error("❌ ERROR DE API - Response Status: " + e.getApiResponse().getStatusCode());
            }

            throw new RuntimeException("Error de API MercadoPago: " + e.getMessage() + " - Status: " + e.getStatusCode());
        } catch (MPException e) {
            log.error("❌ ERROR DE SDK: " + e.getMessage());
            throw new RuntimeException("Error de SDK MercadoPago: " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ ERROR GENÉRICO: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error creando preferencia: " + e.getMessage());
        }
    }

    private PreferenceItemRequest buildItem(CheckoutItem item) {
        return PreferenceItemRequest.builder()
                .title(item.getTitle())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .build();
    }


    public Payment getPayment(Long paymentId) {
        try {
            return paymentClient.get(paymentId);
        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo pago: " + e.getMessage());
        }
    }
}
