package com.example.scrapetok.application;

import com.example.scrapetok.application.apifyservice.ApifyServerConnection;
import com.example.scrapetok.application.apifyservice.JsonProcessor;
import com.example.scrapetok.domain.GeneralAccount;
import com.example.scrapetok.domain.UserApifyCallHistorial;
import com.example.scrapetok.domain.UserApifyFilters;
import com.example.scrapetok.domain.DTO.UserFiltersRequestDTO;
import com.example.scrapetok.exception.ApifyConnectionException;
import com.example.scrapetok.exception.ResourceNotFoundException;
import com.example.scrapetok.repository.GeneralAccountRepository;
import com.example.scrapetok.repository.UserApifyFilterRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserApifyCallService {
    @Value("${apify.token}")
    private String apifyToken;
    @Autowired
    private UserApifyFilterRepository userApifyFilterRepository;
    @Autowired
    private GeneralAccountRepository generalAccountRepository;
    @Autowired
    private ApifyServerConnection apifyServerConnection;
    @Autowired
    private JsonProcessor jsonProcessor;
    @Autowired
    private ModelMapper modelMapper;


    @Transactional
    public List<Map<String,Object>> apifyconnection(UserFiltersRequestDTO request) {
        // Obtener usuario que hace request
        GeneralAccount user = generalAccountRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Obtener historial del usuario
        UserApifyCallHistorial historial = user.getHistorial();
        if (historial == null) {
            historial = new UserApifyCallHistorial();
            historial.setUser(user);
            user.setHistorial(historial);
        }

        // Crear nuevo filtro del usuario
        UserApifyFilters filter = modelMapper.map(request, UserApifyFilters.class);
        filter.setId(null);
        filter.setHistorial(historial);


        // Hacer llamado a APIFY
        Map<String, Object> jsonInput = new HashMap<>();
        // TOKEN ADMINISTRADOR DE APIFY
        jsonInput.put("apifyToken", apifyToken);
        jsonInput.put("excludePinnedPosts", true);
        jsonInput.put("resultsPerPage", request.getNlastPostByHashtags());


        // Scrapeo por Hashtags
        if (request.getHashtags() != null && !request.getHashtags().isEmpty() && request.getNlastPostByHashtags() != null) {
            List<String> video = new ArrayList<>();
            video.add("videos");
            jsonInput.put("profileScrapeSections",video);
            jsonInput.put("profileSorting","latest");
            List<String> hashtags = Arrays.stream(request.getHashtags().split(",")).map(String::trim).collect(Collectors.toList());
            jsonInput.put("hashtags", hashtags);
        }

        // Scrapeo por profile
        if ((request.getDateFrom() != null) && (request.getDateTo() != null) && (request.getTiktokAccount() != null)) {
            String lowerTiktokAccounts = request.getTiktokAccount().toLowerCase();
            List<String> tiktokUsername = Arrays.stream(lowerTiktokAccounts.split(",")).map(String::trim).collect(Collectors.toList());
            jsonInput.put("profiles", tiktokUsername);
            jsonInput.put("oldestPostDate", request.getDateFrom());
            jsonInput.put("newestPostDate", request.getDateTo());
        }

        // Scrapeo por palabras clave
        if (request.getKeyWords() != null) {
            List<String> video = new ArrayList<>();
            video.add("videos");
            jsonInput.put("profileScrapeSections",video);
            jsonInput.put("profileSorting","latest");
            List<String> keywords = Arrays.stream(request.getKeyWords().split(",")).map(String::trim).collect(Collectors.toList());
            jsonInput.put("searchQueries", keywords);
        }

        // DEBUG: Mostrar el JSON que se enviará
        System.out.println(jsonInput);

        // Hacer llamado a APIFY
        Map<String, Object> ApifyResponse;
        try {
            ApifyResponse = apifyServerConnection.fetchDataFromApify(jsonInput, filter);
        } catch (Exception e) {
            throw new ApifyConnectionException("Could not connect to the Apify server.");
        }

        // Procesar data extraída de APIFY
        List<Map<String, Object>> processedData = jsonProcessor.processJson(ApifyResponse, user, historial);
        // Guardar filter con todos los cambios
        userApifyFilterRepository.save(filter);
        // Guardar UserAccount
        generalAccountRepository.save(user);
        return processedData;
    }
}
