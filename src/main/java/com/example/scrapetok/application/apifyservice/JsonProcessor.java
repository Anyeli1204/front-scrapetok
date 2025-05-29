package com.example.scrapetok.application.apifyservice;

import com.example.scrapetok.domain.*;
import com.example.scrapetok.exception.ResourceNotFoundException;
import com.example.scrapetok.repository.AdminTikTokMetricsRepository;
import com.example.scrapetok.repository.UserApifyCallHistorialRepository;
import com.example.scrapetok.repository.UserTiktokMetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Component
public class JsonProcessor {
    private final List<Map<String, Object>> lastProcessedData = new ArrayList<>();
    @Autowired
    private UserTiktokMetricsRepository userTiktokMetricsRepository;
    @Autowired
    private AdminTikTokMetricsRepository adminTiktokMetricsRepository;
    @Autowired
    private UserApifyCallHistorialRepository userApifyCallHistorialRepository;


    // Logica para User -> Procesar user scraping
    public List<Map<String, Object>> processJson(Map<String,Object> jsonResponse, GeneralAccount user, UserApifyCallHistorial historial) throws ResourceNotFoundException {
        List<UserTiktokMetrics> metricasList = new ArrayList<>();
        // Evita acumulación de datos de ejecuciones anteriores
        lastProcessedData.clear();
        if (!jsonResponse.containsKey("Success")
                || jsonResponse.get("Success") == null) {
            throw new IllegalStateException(
                    "Apify no retornó datos válidos en la clave 'Success'");
        }
        if (jsonResponse.containsKey("Error")) {
            throw new ResourceNotFoundException("No data found");
        }
        //Fecha y hora de Perú
        TimeZone timeZone = TimeZone.getTimeZone("America/Lima");
        ZoneId zoneId = timeZone.toZoneId();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) jsonResponse.get("Success");

        // Iterar sobre cada elemento del JSON
        for (Map<String,Object> item : items) {
            Map<String, Object> dataMap = new LinkedHashMap<>();
            UserTiktokMetrics metrica = new UserTiktokMetrics();

            // Datos de la cuenta
            String cuentaInexistente = "";
            if (!item.containsKey("authorMeta") || !(item.get("authorMeta") instanceof Map)) {
                cuentaInexistente = item.containsKey("input") ? item.get("input").toString() : "";
                System.out.printf("Apify proccess: Account " + cuentaInexistente + " doesn't exist");
            }

            String nombreCuenta = cuentaInexistente;
            String region = "Not found: N/A";
            if (item.containsKey("authorMeta") && item.get("authorMeta") instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> authorMeta = (Map<String, Object>) item.get("authorMeta");
                nombreCuenta = authorMeta.containsKey("name") ? authorMeta.get("name").toString() : cuentaInexistente;
                region = authorMeta.containsKey("region") ? authorMeta.get("region").toString() : "Not found: N/A";
            }

            // Datos del video
            String linkVideo = item.getOrDefault("webVideoUrl","Not found: N/A").toString();
            String postcode = (!linkVideo.equals("Not found: N/A")) ? linkVideo.substring(linkVideo.lastIndexOf("/") + 1) : "Not found: N/A";
            String fechaHoraVideo = item.getOrDefault("createTimeISO","Not found: N/A").toString();
            LocalDate fechaVideo;
            LocalTime horaVideo;
            if (fechaHoraVideo.equals("Not found: N/A")) {
                fechaVideo = null;
                horaVideo = null;
            } else {
                Object[] fechaHora = formatearFechaHora(fechaHoraVideo);
                fechaVideo = (LocalDate) fechaHora[0];
                horaVideo = (LocalTime) fechaHora[1];
            }

            // Métricas del video
            int views = item.containsKey("playCount") && item.get("playCount") instanceof Number ? ((Number) item.get("playCount")).intValue() : 0;
            int comentarios = item.containsKey("commentCount") && item.get("commentCount") instanceof Number ? ((Number) item.get("commentCount")).intValue() : 0;
            int likes = item.containsKey("diggCount") && item.get("diggCount") instanceof Number ? ((Number) item.get("diggCount")).intValue() : 0;
            int guardados = item.containsKey("collectCount") && item.get("collectCount") instanceof Number ? ((Number) item.get("collectCount")).intValue() : 0;
            int compartidos = item.containsKey("shareCount") && item.get("shareCount") instanceof Number ? ((Number) item.get("shareCount")).intValue() : 0;

            // Procesar hashtags
            List<String> listaHashtags = new ArrayList<>();
            if (item.containsKey("hashtags") && item.get("hashtags") instanceof List<?> hashtags) {
                for (Object ht : hashtags) {
                    if (ht instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> hashtagMap = (Map<String, Object>) ht;
                        String hashtag = hashtagMap.containsKey("name") ? hashtagMap.get("name").toString() : "";
                        if (!hashtag.isEmpty()) {
                            listaHashtags.add("#" + hashtag);
                        }
                    }
                }
            }
            // Si la lista de hashtags está vacía, usamos "Not found: N/A"
            String final_hashtags = listaHashtags.isEmpty() ? "Not found: N/A" : String.join(", ", listaHashtags);
            int numHashtags = listaHashtags.isEmpty() ? 0 : listaHashtags.size();


            // Datos del sonido
            String idSound = "";
            String nombreSonido = "";
            if (item.containsKey("musicMeta") || (item.get("musicMeta") instanceof Map)) {
                @SuppressWarnings("unchecked")
                Map<String,Object> sound= ( Map<String,Object>) item.get("musicMeta");
                idSound = sound.containsKey("musicId") ? sound.get("musicId").toString() : "";
                nombreSonido = sound.containsKey("musicName") ? sound.get("musicName").toString() : "";
            }
            String URL_final = "Not found: N/A";
            if (!idSound.isEmpty() && !nombreSonido.isEmpty()) {
                nombreSonido = nombreSonido.replaceAll("\\s*-\\s*", "-").replace(" ", "-");
                URL_final = "https://tiktok.com/music/" + nombreSonido + "-" + idSound;
            }

            // Calcular engagement
            double engagement = calcularEngagement(views, comentarios, likes, guardados, compartidos);
            int totalInteracciones = likes + comentarios + compartidos + guardados;

            // Fecha y hora del tracking
            ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
            LocalDate fechaTrackeo = zonedDateTime.toLocalDate();
            LocalTime horaTrackeo = zonedDateTime.toLocalTime().withNano(0); // truncar nanos si deseas

            // Agregar datos que se devuelven al frontend
            dataMap.put("Post code",postcode);
            dataMap.put("Date posted", fechaVideo);
            dataMap.put("Time posted", horaVideo);
            dataMap.put("TikTok Account Username", nombreCuenta);
            dataMap.put("Post Link", linkVideo);
            dataMap.put("Views", views);
            dataMap.put("Likes", likes);
            dataMap.put("Comments", comentarios);
            dataMap.put("Reposted", compartidos);
            dataMap.put("Saves", guardados);
            dataMap.put("Engagement rate", Math.round(engagement * 100.0) / 100.0);
            dataMap.put("Interactions", totalInteracciones);
            dataMap.put("Hashtags", final_hashtags);
            dataMap.put("# of Hashtags", numHashtags);
            dataMap.put("Sound ID", idSound);
            dataMap.put("Sound URL", URL_final);
            dataMap.put("Region of posting", region);
            dataMap.put("Tracking date", fechaTrackeo);
            dataMap.put("Tracking time", horaTrackeo);
            dataMap.put("User", user.getUsername());
            System.out.println(dataMap);

            // Agregar al listado publicación a la lista contenedora de todos.
            lastProcessedData.add(dataMap);

            // Setter de todos los atributos para BD
            if ((!postcode.equals("Not found: N/A")) && fechaVideo != null) {
                metrica.setPostId(postcode);
                metrica.setDatePosted(fechaVideo);
                metrica.setHourPosted(horaVideo);
                metrica.setUsernameTiktokAccount(nombreCuenta);
                metrica.setPostURL(linkVideo);
                metrica.setViews(views);
                metrica.setLikes(likes);
                metrica.setSaves(guardados);
                metrica.setReposts(compartidos);
                metrica.setComments(comentarios);
                metrica.setEngagement(Math.round(engagement * 100.0) / 100.0);
                metrica.setTotalInteractions(totalInteracciones);
                metrica.setHashtags(final_hashtags);
                metrica.setNumberHashtags(numHashtags);
                metrica.setSoundId(idSound);
                metrica.setSoundURL(URL_final);
                metrica.setRegionPost(region);
                metrica.setDateTracking(fechaTrackeo);
                metrica.setTimeTracking(horaTrackeo);
                // Asignar USER q hizo SCRAPEO
                metrica.setUser(user);
                // Agregar a la lista de METRICASLIST general para la BD
                metricasList.add(metrica);

            }
        }
        // Guardar en BD cada registro
        if (!metricasList.isEmpty()) {
            userTiktokMetricsRepository.saveAll(metricasList);
            userApifyCallHistorialRepository.save(historial);
        }
        return lastProcessedData;
    }


    // Logica para Admin -> Procesar admin scraping
    public List<Map<String, Object>> processJson(Map<String,Object> jsonResponse, AdminProfile admin) throws ResourceNotFoundException {
        List<AdminTiktokMetrics> metricasList = new ArrayList<>();
        lastProcessedData.clear(); // Evita acumulación de datos de ejecuciones anteriores
        if (jsonResponse.containsKey("Error")) {
            throw new ResourceNotFoundException("No data found");
        }
        //Fecha y hora de Perú
        TimeZone timeZone = TimeZone.getTimeZone("America/Lima");
        ZoneId zoneId = timeZone.toZoneId();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) jsonResponse.get("Success");

        // Iterar sobre cada elemento del JSON
        for (Map<String,Object> item : items) {
            Map<String, Object> dataMap = new LinkedHashMap<>();
            AdminTiktokMetrics metrica = new AdminTiktokMetrics();

            // Datos de la cuenta
            String cuentaInexistente = "";
            if (!item.containsKey("authorMeta") || !(item.get("authorMeta") instanceof Map)) {
                cuentaInexistente = item.containsKey("input") ? item.get("input").toString() : "";
                System.out.printf("Apify proccess: Account " + cuentaInexistente + " doesn't exist");
            }

            String nombreCuenta = cuentaInexistente;
            String region = "Not found: N/A";
            if (item.containsKey("authorMeta") && item.get("authorMeta") instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> authorMeta = (Map<String, Object>) item.get("authorMeta");
                nombreCuenta = authorMeta.containsKey("name") ? authorMeta.get("name").toString() : cuentaInexistente;
                region = authorMeta.containsKey("region") ? authorMeta.get("region").toString() : "Not found: N/A";

            }

            // Datos del video
            String linkVideo = item.getOrDefault("webVideoUrl","Not found: N/A").toString();
            String postcode = !(linkVideo.equals("Not found: N/A")) ? linkVideo.substring(linkVideo.lastIndexOf("/") + 1) : "Not found: N/A";
            String fechaHoraVideo = item.getOrDefault("createTimeISO","Not found: N/A").toString();
            LocalDate fechaVideo;
            LocalTime horaVideo;
            if (fechaHoraVideo.equals("Not found: N/A")) {
                fechaVideo = null;
                horaVideo = null;
            } else {
                Object[] fechaHora = formatearFechaHora(fechaHoraVideo);
                fechaVideo = (LocalDate) fechaHora[0];
                horaVideo = (LocalTime) fechaHora[1];
            }

            // Métricas del video
            int views = item.containsKey("playCount") && item.get("playCount") instanceof Number ? ((Number) item.get("playCount")).intValue() : 0;
            int comentarios = item.containsKey("commentCount") && item.get("commentCount") instanceof Number ? ((Number) item.get("commentCount")).intValue() : 0;
            int likes = item.containsKey("diggCount") && item.get("diggCount") instanceof Number ? ((Number) item.get("diggCount")).intValue() : 0;
            int guardados = item.containsKey("collectCount") && item.get("collectCount") instanceof Number ? ((Number) item.get("collectCount")).intValue() : 0;
            int compartidos = item.containsKey("shareCount") && item.get("shareCount") instanceof Number ? ((Number) item.get("shareCount")).intValue() : 0;

            // Procesar hashtags
            List<String> listaHashtags = new ArrayList<>();
            if (item.containsKey("hashtags") && item.get("hashtags") instanceof List<?> hashtags) {
                for (Object ht : hashtags) {
                    if (ht instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> hashtagMap = (Map<String, Object>) ht;
                        String hashtag = hashtagMap.containsKey("name") ? hashtagMap.get("name").toString() : "";
                        if (!hashtag.isEmpty()) {
                            listaHashtags.add("#" + hashtag);
                        }
                    }
                }
            }
            // Si la lista de hashtags está vacía, usamos "Not found: N/A"
            String final_hashtags = listaHashtags.isEmpty() ? "Not found: N/A" : String.join(", ", listaHashtags);
            int numHashtags = listaHashtags.isEmpty() ? 0 : listaHashtags.size();


            // Datos del sonido
            String idSound = "";
            String nombreSonido = "";
            if (item.containsKey("musicMeta") || (item.get("musicMeta") instanceof Map)) {
                @SuppressWarnings("unchecked")
                Map<String,Object> sound= ( Map<String,Object>) item.get("musicMeta");
                idSound = sound.containsKey("musicId") ? sound.get("musicId").toString() : "";
                nombreSonido = sound.containsKey("musicName") ? sound.get("musicName").toString() : "";
            }
            String URL_final = "Not found: N/A";
            if (!idSound.isEmpty() && !nombreSonido.isEmpty()) {
                nombreSonido = nombreSonido.replaceAll("\\s*-\\s*", "-").replace(" ", "-");
                URL_final = "https://tiktok.com/music/" + nombreSonido + "-" + idSound;
            }

            // Calcular engagement
            double engagement = calcularEngagement(views, comentarios, likes, guardados, compartidos);
            int totalInteracciones = likes + comentarios + compartidos + guardados;

            // Fecha y hora del tracking
            ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
            LocalDate fechaTrackeo = zonedDateTime.toLocalDate();
            LocalTime horaTrackeo = zonedDateTime.toLocalTime().withNano(0); // truncar nanos si deseas

            // Agregar datos que se devuelven al frontend
            dataMap.put("Post code",postcode);
            dataMap.put("Date posted", fechaVideo);
            dataMap.put("Time posted", horaVideo);
            dataMap.put("TikTok Account Username", nombreCuenta);
            dataMap.put("Post Link", linkVideo);
            dataMap.put("Views", views);
            dataMap.put("Likes", likes);
            dataMap.put("Comments", comentarios);
            dataMap.put("Reposted", compartidos);
            dataMap.put("Saves", guardados);
            dataMap.put("Engagement rate", Math.round(engagement * 100.0) / 100.0);
            dataMap.put("Interactions", totalInteracciones);
            dataMap.put("Hashtags", final_hashtags);
            dataMap.put("# of Hashtags", numHashtags);
            dataMap.put("Sound ID", idSound);
            dataMap.put("Sound URL", URL_final);
            dataMap.put("Region of posting", region);
            dataMap.put("Tracking date", fechaTrackeo);
            dataMap.put("Tracking time", horaTrackeo);
            dataMap.put("Admin", admin.getUser().getUsername());
            System.out.println("Debug:" + dataMap);
            // Agregar al listado publicación a la lista contenedora de todos.
            lastProcessedData.add(dataMap);

            // Setter de todos los atributos para BD
            if ((!postcode.equals("Not found: N/A")) && fechaVideo != null) {
                metrica.setPostId(postcode);
                metrica.setDatePosted(fechaVideo);
                metrica.setHourPosted(horaVideo);
                metrica.setUsernameTiktokAccount(nombreCuenta);
                metrica.setPostURL(linkVideo);
                metrica.setViews(views);
                metrica.setLikes(likes);
                metrica.setSaves(guardados);
                metrica.setReposts(compartidos);
                metrica.setComments(comentarios);
                metrica.setEngagement(Math.round(engagement * 100.0) / 100.0);
                metrica.setTotalInteractions(totalInteracciones);
                metrica.setHashtags(final_hashtags);
                metrica.setNumberHashtags(numHashtags);
                metrica.setSoundId(idSound);
                metrica.setSoundURL(URL_final);
                metrica.setRegionPost(region);
                metrica.setDateTracking(fechaTrackeo);
                metrica.setTimeTracking(horaTrackeo);
                // Cada post tiene un Admin
                metrica.setAdmin(admin);
                // Agregar a la lista de METRICASLIST general para la BD
                metricasList.add(metrica);
            }
        }
        // Guardar en BD cada registro
        if (!metricasList.isEmpty()) {
            adminTiktokMetricsRepository.saveAll(metricasList);
        }
        return lastProcessedData;
    }

    private static Object[] formatearFechaHora(String fechaHoraISO) {
        try {
            LocalDate fecha = LocalDate.parse(fechaHoraISO.substring(0, 10));  // Extrae YYYY-MM-DD
            LocalTime hora = LocalTime.parse(fechaHoraISO.substring(11, 19)); // Extrae HH:MM:SS
            return new Object[]{fecha, hora};
        } catch (Exception e) {
            return new String[]{"Not found: N/A", "Not found: N/A"};
        }
    }

    private static double calcularEngagement(int views, int comentarios, int likes, int guardados, int compartidos) {
        if (views == 0) return 0;
        return ((double) (likes + comentarios + guardados + compartidos) / views) * 100;
    }
}



