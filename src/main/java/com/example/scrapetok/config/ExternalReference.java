package com.example.scrapetok.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa el JSON que enviarás como externalReference en MercadoPago
 * {
 *    "userId": 123,
 *    "tier": "PRO"
 * }
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalReference {
    private Long userId;
    private String tier;
}
