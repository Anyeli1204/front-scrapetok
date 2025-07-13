package com.example.scrapetok.config;


import com.example.scrapetok.application.JwtService;
import com.example.scrapetok.domain.GeneralAccount;
import com.example.scrapetok.repository.GeneralAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtService jwtTokenProvider;

    @Autowired
    private GeneralAccountRepository userRepository; // Para obtener userId desde username

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getCommand() == StompCommand.CONNECT) {
            String token = extractToken(accessor);

            if (token == null || token.isBlank()) {
                throw new MessagingException("Token no proporcionado");
            }

            if (!jwtTokenProvider.validateToken(token)) {
                throw new MessagingException("Token inválido");
            }

            Authentication auth = jwtTokenProvider.getAuthentication(token);
            if (auth == null) {
                throw new MessagingException("No se pudo obtener autenticación del token");
            }

            SecurityContextHolder.getContext().setAuthentication(auth);
            accessor.getSessionAttributes().put("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        }
        else if (accessor.getCommand() == StompCommand.SUBSCRIBE || accessor.getCommand() == StompCommand.SEND) {
            Object securityContext = accessor.getSessionAttributes().get("SPRING_SECURITY_CONTEXT");

            if (securityContext instanceof SecurityContext) {
                SecurityContextHolder.setContext((SecurityContext) securityContext);
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();

                if (auth != null && auth.isAuthenticated()) {
                    if (accessor.getCommand() == StompCommand.SUBSCRIBE) {
                        validateUserSubscription(accessor, auth);
                    }
                } else {
                    throw new MessagingException("Sesión no autenticada");
                }
            } else {
                throw new MessagingException("Sesión no autenticada");
            }
        }

        return message;
    }

    private String extractToken(StompHeaderAccessor accessor) {
        List<String> authHeaders = accessor.getNativeHeader("Authorization");
        if (authHeaders == null || authHeaders.isEmpty()) {
            authHeaders = accessor.getNativeHeader("authorization");
        }
        if (authHeaders == null || authHeaders.isEmpty()) {
            return null;
        }
        String bearerToken = authHeaders.get(0);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void validateUserSubscription(StompHeaderAccessor accessor, Authentication auth) {
        String destination = accessor.getDestination();

        if (destination != null && destination.startsWith("/user/")) {
            String[] parts = destination.split("/");
            if (parts.length >= 3) {
                String userIdFromDestination = parts[2];

                // auth.getName() ahora es el email único
                String authenticatedEmail = auth.getName();

                // Obtener el userId usando el email único
                Long authenticatedUserId = getUserIdFromEmail(authenticatedEmail);

                if (authenticatedUserId != null) {
                    if (!userIdFromDestination.equals(authenticatedUserId.toString())) {
                        throw new MessagingException("No tienes permiso para suscribirte a las notificaciones de otro usuario");
                    }
                } else {
                    throw new MessagingException("No se encontró el usuario autenticado para validar suscripción.");
                }
            }
        }
    }

    private Long getUserIdFromEmail(String email) {
        return userRepository.findByEmail(email)
                .map(GeneralAccount::getId)
                .orElse(null);
    }
}