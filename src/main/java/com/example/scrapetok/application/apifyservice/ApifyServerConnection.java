package com.example.scrapetok.application.apifyservice;

import com.example.scrapetok.domain.UserApifyFilters;
import com.example.scrapetok.domain.enums.ApifyRunStatus;
import com.example.scrapetok.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;



@Service
public class ApifyServerConnection {
    @Value("${apify.url}")
    private String apifyUrl;

    // Lógica para user -> user scraping
    public Map<String,Object> fetchDataFromApify(Map<String,Object> jsonInput, UserApifyFilters filter) throws IOException, ResourceNotFoundException, IllegalStateException {
        // Inicio contador
        long inicio = System.currentTimeMillis();
        // Convertir el diccionario a un JSON usando Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(jsonInput);
        String apiURL = apifyUrl;

        // Setup HTTP connection
        String ApiURL = apiURL + "/APIFYCALL";
        URL url = new URL(ApiURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(true);

        // Send request body
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Read response
        int responseCode = conn.getResponseCode();
        InputStream inputStream = (responseCode == HttpURLConnection.HTTP_OK)
                ? conn.getInputStream()
                : conn.getErrorStream();


        try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }


            String body = response.toString();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                filter.setApifyRunStatus(ApifyRunStatus.FAILED);
                // Puedes lanzar excepción, o devolver un Map:
                return Map.of(
                        "Error",
                        "HTTP " + responseCode + ": " + body
                );
            }

            Map<String, Object> responseMap = objectMapper.readValue(response.toString(), new TypeReference<>() {});
            String prettyJson = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(responseMap);
            System.out.println(prettyJson);

            // Fin del contador
            long fin = System.currentTimeMillis();
            // tiempo de ejecución en milisegundos -> APIFY
            int tiempoTotal = (int) (fin - inicio);
            filter.setExecutionTime(tiempoTotal);

            // Interpret API response
            if (responseMap.containsKey("Success")) {
                filter.setApifyRunStatus(ApifyRunStatus.COMPLETED);
                return Map.of("Success", responseMap.get("Success"));
            } else if (responseMap.containsKey("Error")) {
                filter.setApifyRunStatus(ApifyRunStatus.FAILED);
                return Map.of("Error", responseMap.get("Error"));
            } else {
                filter.setApifyRunStatus(ApifyRunStatus.FAILED);
                return Map.of("Error", "Respuesta inesperada: " + body);
            }
        }
    }


    // Lógica para administrador -> scraping general
    public Map<String,Object> fetchDataFromApify(Map<String,Object> jsonInput) throws IOException, ResourceNotFoundException, IllegalStateException {
        // Convertir el diccionario a un JSON usando Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(jsonInput);
        String apiURL = apifyUrl;

        // Setup HTTP connection
        String ApiURL = apiURL + "/APIFYCALL";
        URL url = new URL(ApiURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(true);

        // Send request body
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Read response
        int responseCode = conn.getResponseCode();
        InputStream inputStream = (responseCode == HttpURLConnection.HTTP_OK)
                ? conn.getInputStream()
                : conn.getErrorStream();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            Map<String, Object> responseMap = objectMapper.readValue(response.toString(), new TypeReference<>() {});
            // Interpret API response
            if (responseMap.containsKey("Success")) {
                return Map.of("Success", responseMap.get("Success"));
            } else if (responseMap.containsKey("Error")) {
                return Map.of("Error", responseMap.get("Error"));
            }
            throw new IllegalStateException("Internal Server error: Can't establish connection to Apify server");
        }
    }
}
