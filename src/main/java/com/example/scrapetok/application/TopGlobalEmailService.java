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
        body.append("Hello!\n\n");
        body.append("Here are today's top viral TikToks by hashtag / keyWord. Discover what’s trending globally now:\n\n");

        for (TopGlobalEmailDTO post : posts) {
            body.append("🔹 #").append(post.getUsedHashTag()).append("\n");
            body.append("🎬 Video by: @").append(post.getUsernameTiktokAccount()).append("\n");
            body.append("📅 Date Posted: ").append(post.getDatePosted()).append("\n");
            body.append("👀 Views: ").append(String.format("%,d", post.getViews())).append("\n");
            body.append("❤️ Likes: ").append(String.format("%,d", post.getLikes())).append("\n");
            body.append("📊 Engagement: ").append(String.format("%.2f%%", post.getEngagement())).append("\n");
            body.append("📎 Watch here: ").append(post.getPostURL()).append("\n");
            body.append("------------------------------------------------------------\n\n");
        }

        body.append("This summary is generated automatically based on latest top-performing global content.\n");
        body.append("Your ScrapeTok Team");


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
