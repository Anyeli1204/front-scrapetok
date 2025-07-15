package com.example.scrapetok.application;

import com.example.scrapetok.application.emailservice.AlertEmailEvent;
import com.example.scrapetok.domain.AdminProfile;
import com.example.scrapetok.domain.DTO.TopGlobalEmailDTO;
import com.example.scrapetok.domain.DailyAlerts;
import com.example.scrapetok.domain.GeneralAccount;
import com.example.scrapetok.exception.ResourceNotFoundException;
import com.example.scrapetok.repository.AdminProfileRepository;
import com.example.scrapetok.repository.DailyAlertsRepository;
import com.example.scrapetok.repository.GeneralAccountRepository;
import com.google.common.collect.Lists;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional
public class TopGlobalEmailService {
    @Autowired
    private GeneralAccountRepository generalAccountRepository;
    @Autowired
    private AdminProfileRepository adminProfileRepository;
    @Autowired
    private DailyAlertsRepository dailyAlertsRepository;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void sendTopGlobalTextEmail(List<TopGlobalEmailDTO> posts) throws ResourceNotFoundException, IllegalArgumentException {
        if (posts == null || posts.isEmpty()) {
            throw new IllegalArgumentException("La lista de publicaciones está vacía.");
        }
        Long adminId = posts.get(0).getAdminId();
        AdminProfile admin =  adminProfileRepository.findById(adminId).orElseThrow(() -> new ResourceNotFoundException("Admin with id " + adminId + " Not Found"));
        List<GeneralAccount> users = generalAccountRepository.findAll();
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No hay usuarios registrados para recibir el correo.");
        }

        String subject = "ScrapeTok: 🌍 Today’s Top Global TikTok Hits by Hashtag / KeyWord";
        StringBuilder body = new StringBuilder();
        body.append("<div style=\"font-family: 'Segoe UI', sans-serif; color: #333; padding: 10px; max-width: 600px; margin: auto;\">");

        body.append("<h2 style=\"color: #7e22ce; text-align: center;\">🌍 Top Global TikToks del Día</h2>");
        body.append("<p>Hola 👋,</p>");
        body.append("<p>Te compartimos el resumen de los TikToks más virales del día según hashtags y palabras clave.</p>");
        body.append("<hr style=\"margin: 20px 0;\"/>");

        for (TopGlobalEmailDTO post : posts) {
            body.append("<div style=\"border: 1px solid #eee; border-radius: 10px; padding: 15px; margin-bottom: 20px; background-color: #fafafa;\">");
            body.append("<h3 style=\"margin-top: 0; color: #4ba3c7;\">").append(post.getUsedHashTag()).append("</h3>");
            body.append("<p><strong>🎬 Creador:</strong> @").append(post.getUsernameTiktokAccount()).append("</p>");
            body.append("<p><strong>📅 Fecha:</strong> ").append(post.getDatePosted()).append("</p>");
            body.append("<p><strong>👀 Vistas:</strong> ").append(String.format("%,d", post.getViews())).append("</p>");
            body.append("<p><strong>❤️ Likes:</strong> ").append(String.format("%,d", post.getLikes())).append("</p>");
            body.append("<p><strong>📊 Engagement:</strong> ").append(String.format("%.2f", post.getEngagement())).append("%</p>");
            body.append("<p><a href=\"").append(post.getPostURL()).append("\" style=\"color: #7e22ce; text-decoration: none; font-weight: bold;\">🔗 Ver publicación</a></p>");
            body.append("</div>");
        }

        body.append("<p style=\"font-size: 0.95rem; color: #666;\">Este resumen ha sido generado automáticamente con base en los contenidos más virales del día en ScrapeTok.</p>");
        body.append("<p style=\"margin-top: 20px; font-weight: bold;\">— El equipo de ScrapeTok 🚀</p>");
        body.append("</div>");
        DailyAlerts alert = new DailyAlerts();
        alert.setUserEmails(new HashSet<>(users));
        alert.setAdmin(admin);
        alert.setSubject(subject);
        alert.setBody(body.toString());
        ZonedDateTime zonedDateTime = obtenerFechaYHoraDePeru();
        alert.setPostedDate(zonedDateTime.toLocalDate());
        alert.setPostedTime(zonedDateTime.toLocalTime().withNano(0));
        dailyAlertsRepository.save(alert);
        adminProfileRepository.save(admin);


        // Dividir en batches de 50 usuarios
        List<List<GeneralAccount>> batches = Lists.partition(users,50);
        // Publicar los eventos en grupos (50) con pausa
        for (List<GeneralAccount> batch : batches) {
            for (GeneralAccount usuario : batch) {
                applicationEventPublisher.publishEvent(new AlertEmailEvent(this, usuario.getEmail(), subject, body.toString()));
            }
            try {
                // Esperar 2 segundos antes del siguiente batch
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // Restaurar estado de interrupción
                Thread.currentThread().interrupt();
            }
        }
    }

    private ZonedDateTime obtenerFechaYHoraDePeru() {
        return ZonedDateTime.now(ZoneId.of("America/Lima"));
    }
}
