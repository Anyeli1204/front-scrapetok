package com.example.scrapetok.application;

import com.example.scrapetok.application.apifyservice.ApifyServerConnection;
import com.example.scrapetok.application.apifyservice.JsonProcessor;
import com.example.scrapetok.domain.AdminProfile;
import com.example.scrapetok.domain.DTO.AdminFilterRequestDTO;
import com.example.scrapetok.exception.ApifyConnectionException;
import com.example.scrapetok.exception.ResourceNotFoundException;
import com.example.scrapetok.exception.ServiceUnavailableException;
import com.example.scrapetok.repository.AdminProfileRepository;
import com.example.scrapetok.repository.AdminTikTokMetricsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class AdminApifyCallService {
    @Value("${apify.token}")
    private String apifyToken;
    @Autowired
    private ApifyServerConnection apifyServerConnection;
    @Autowired
    private JsonProcessor jsonProcessor;
    @Autowired
    private AdminProfileRepository adminProfileRepository;
    @Autowired
    private AdminTikTokMetricsRepository adminTikTokMetricsRepository;

    public List<Object> apifyconnection(AdminFilterRequestDTO request) throws Exception {
        try {
            List<Object> dataSet = new ArrayList<>();
            AdminProfile admin = adminProfileRepository.findById(request.getAdminId())
                    .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

            // Hacer llamado a APIFY
            Map<String, Object> jsonInput = new HashMap<>();
            // TOKEN ADMINISTRADOR DE APIFY
            jsonInput.put("apifyToken", apifyToken);
            jsonInput.put("excludePinnedPosts", true);
            // Por defecto, ADMIN scrapea 20 videos por hashtags
            jsonInput.put("resultsPerPage", 10);
            List<String> video = new ArrayList<>();
            video.add("videos");
            jsonInput.put("profileScrapeSections", video);
            jsonInput.put("profileSorting", "latest");


            if (request.getHashtags() != null) {
                List<String> hashtags = Arrays.stream(request.getHashtags().split(",")).map(String::trim).collect(Collectors.toList());
                jsonInput.put("hashtags", hashtags);
            }

            // Admin scrapea por h
            if (request.getKeyWords() != null) {
                List<String> keywords = Arrays.stream(request.getKeyWords().split(",")).map(String::trim).collect(Collectors.toList());
                jsonInput.put("searchQueries", keywords);
            }


            // DEBUG: Mostrar el JSON que se enviará
            System.out.println("JSON enviado: " + jsonInput);

            Map<String, Object> ApifyResponse;
            try {
                ApifyResponse = apifyServerConnection.fetchDataFromApify(jsonInput);
            } catch (Exception e) {
                throw new ApifyConnectionException("Error al conectar con Apify: " + e.getMessage());
            }

            // PROCESAR DATOS Y GUARDAR EN BD
            List<Map<String, Object>> data = jsonProcessor.processJson(ApifyResponse, admin);
            dataSet.add(data);

            // CONSULTAS SQL A DATOS MÁS RECIENTES GUARDADOS DÍA DE HOY OBTENIDO POR ADMIN -> PROTOCOLO CUANDO ADMIN HACE SCRAPEO GENERAL
            List<AdminTikTokMetricsRepository.RegionVideoCount> resultados = adminTikTokMetricsRepository.countTodayRecentVideosByRegion();
            dataSet.add(resultados);

            List<AdminTikTokMetricsRepository.HashtagViewCount> obtenerVistasPorHashtagHoy = adminTikTokMetricsRepository.countTodayViewsByHashtag();
            dataSet.add(obtenerVistasPorHashtagHoy);

            List<AdminTikTokMetricsRepository.SoundViewCount> ViewsVsIdSound = adminTikTokMetricsRepository.countTodayViewsBySound();
            dataSet.add(ViewsVsIdSound);

            List<AdminTikTokMetricsRepository.RegionMetricsCount> RegionVsCount = adminTikTokMetricsRepository.countTodayViewsAndLikesByRegion();
            dataSet.add(RegionVsCount);


            /*List<Map<String,Object>> usernameVsViews = adminTikTokMetricsRepository.findViewsGroupedByUsernameForToday();
            List<Map<String,Object>> musicIdVsViews = adminTikTokMetricsRepository.findViewsGroupedByMusicIdForToday();
            List<Map<String,Object>> DatePostVsViews = adminTikTokMetricsRepository.findViewsGroupedByPostDateTrackedToday();
            List<HashtagTrendDTO> frecuencyAndViewsPerHashtag = adminTikTokMetricsRepository.findTopHashtagsTrendingToday();
            dataSet.add(usernameVsViews);
            dataSet.add(musicIdVsViews);
            dataSet.add(DatePostVsViews);
            dataSet.add(frecuencyAndViewsPerHashtag);*/

            return dataSet;
        }
        catch (ApifyConnectionException | ResourceNotFoundException | ServiceUnavailableException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceUnavailableException("Error inesperado en el servicio AdminApifyCallService: " + e.getMessage());
        }
    }
}

