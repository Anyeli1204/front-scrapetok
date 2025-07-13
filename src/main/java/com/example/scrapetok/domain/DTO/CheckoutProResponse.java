package com.example.scrapetok.domain.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutProResponse {
    private String preferenceId;
    private String initPoint;
}
