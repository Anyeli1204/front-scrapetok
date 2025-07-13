package com.example.scrapetok.application;


import com.example.scrapetok.domain.GeneralAccount;
import com.example.scrapetok.repository.GeneralAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final GeneralAccountRepository accountRepository;

    public PaymentService(GeneralAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void activarSuscripcion(Long userId, String paymentId, String tier) {
        GeneralAccount user = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        user.setSubscriptionActive(true);
        user.setSubscriptionTier(tier);

        accountRepository.save(user);
        System.out.println("✅ Suscripción activada para el usuario con ID: " + userId + ", Tier: " + tier);
    }
}