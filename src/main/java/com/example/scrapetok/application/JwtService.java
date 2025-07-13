package com.example.scrapetok.application;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;


@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    /**
     * -- GETTER --
     *  Devuelve el tiempo de expiración configurado del JWT en milisegundos.
     */
    @Getter
    private final long expirationMillis = 1000L * 60 * 60 * 24; // 24 horas

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        log.info("✅ JwtService inicializado correctamente.");
    }

    /**
     * Genera un JWT con el email del usuario como subject y expiración fija.
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email) // Email es el identificador único en tu sistema
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida el token JWT verificando firma y expiración.
     * @param token JWT recibido
     * @return true si es válido, false si es inválido o expirado
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("❌ Token expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("❌ Token no soportado: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("❌ Token mal formado: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("❌ Firma inválida en el token: {}", e.getMessage());
        } catch (JwtException e) {
            log.warn("❌ Error general de JWT: {}", e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error inesperado al validar el token: {}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * Obtiene la autenticación del usuario a partir del token JWT.
     * El JWT debe tener el email del usuario como 'subject'.
     *
     * @param token JWT válido
     * @return Authentication para Spring Security o null si falla
     */
    public Authentication getAuthentication(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();

            if (email == null || email.isBlank()) {
                log.warn("❌ El token no contiene un email válido en el subject.");
                return null;
            }

            User principal = new User(email, "", Collections.emptyList());
            return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
        } catch (ExpiredJwtException e) {
            log.warn("❌ No se puede obtener autenticación: token expirado.");
        } catch (JwtException e) {
            log.warn("❌ Error al obtener autenticación del token: {}", e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error inesperado al obtener autenticación del token: {}", e.getMessage(), e);
        }
        return null;
    }
}