package com.example.scrapetok.application;

import com.example.scrapetok.domain.DTO.UserDBQueryRequest;
import com.example.scrapetok.domain.DTO.UserTiktokMetricsResponseDTO;
import com.example.scrapetok.domain.DTO.projection.MetricData;
import com.example.scrapetok.domain.UserTiktokMetrics;
import com.example.scrapetok.repository.UserTiktokMetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DBQueryService {
    @Autowired
    private UserTiktokMetricsRepository userTiktokMetricsRepository;

    //Busca registros de métricas aplicando filtros del request.
    public List<Object> buscarConFiltros(UserDBQueryRequest req) {
        List<Object> encasulador = new ArrayList<>();
        var spec = UserTiktokMetricsSpecification.filterBy(req);
        var sort = Sort.by(
                Sort.Order.desc("datePosted"),
                Sort.Order.desc("hourPosted")
        );

        List<UserTiktokMetrics> metricasFiltradas = userTiktokMetricsRepository.findAll(spec, sort);
        encasulador.add(metricasFiltradas.stream()
                .map(this::toDto)
                .collect(Collectors.toList()));

        List<MetricData> dashboard = new ArrayList<>();

        // 3.1) Datos para graph SumViews vs Hashtags
        if (req.getHashtags() != null) {
        String[] filtros = req.getHashtags().split(",");
        // Normalizas espacios y cadenas vacías:
        List<String> tagsAFiltrar = Arrays.stream(filtros)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        for (String tag : tagsAFiltrar) {
            long sumaVistas = metricasFiltradas.stream()
                    // 1) solo posts cuyo campo hashtags, al dividirlo en lista,
                    //    contenga exactamente ese tag
                    .filter(m -> {
                        String[] postTags = m.getHashtags().split(",");
                        return Arrays.stream(postTags)
                                .map(String::trim)
                                .anyMatch(h -> h.equalsIgnoreCase(tag));
                    })
                    // 2) extraes el número de views y lo sumas
                    .mapToLong(UserTiktokMetrics::getViews)
                    .sum();
            long sumaLikes = metricasFiltradas.stream()
                    .filter(m -> {
                        return Arrays.stream(m.getHashtags().split(","))
                                .map(String::trim)
                                .anyMatch(h -> h.equalsIgnoreCase(tag));
                    })
                    .mapToLong(UserTiktokMetrics::getLikes)
                    .sum();
            long sumInteractions = metricasFiltradas.stream()
                    .filter(m -> {
                        return Arrays.stream(m.getHashtags().split(","))
                                .map(String::trim)
                                .anyMatch(h -> h.equalsIgnoreCase(tag));
                    })
                    .mapToLong(UserTiktokMetrics::getTotalInteractions)
                    .sum();
            double promedioEngagement = metricasFiltradas.stream()
                    .filter(m -> Arrays.stream(m.getHashtags().split(","))
                            .map(String::trim)
                            .anyMatch(h -> h.equalsIgnoreCase(tag))
                    )
                    .mapToDouble(UserTiktokMetrics::getEngagement)
                    .average()
                    .orElse(0.0);
            dashboard.add(new MetricData("MetricByHashtag", tag, sumaVistas,  sumaLikes,(long)promedioEngagement, sumInteractions));
        }}

        if (req.getTiktokUsernames() != null) {
        Map<String, List<UserTiktokMetrics>> porUsuario = metricasFiltradas.stream()
                .filter(m -> m.getUsernameTiktokAccount() != null)
                .collect(Collectors.groupingBy(
                        UserTiktokMetrics::getUsernameTiktokAccount
                ));

        for (Map.Entry<String, List<UserTiktokMetrics>> entry : porUsuario.entrySet()) {
            String username = entry.getKey();
            List<UserTiktokMetrics> listaUsuario = entry.getValue();

            // 1) suma de vistas
            long sumaVistas = listaUsuario.stream()
                    .mapToLong(UserTiktokMetrics::getViews)
                    .sum();

            // 2) suma de likes
            long sumaLikes = listaUsuario.stream()
                    .mapToLong(UserTiktokMetrics::getLikes)
                    .sum();
            long sumInteractions = listaUsuario.stream()
                    .mapToLong(UserTiktokMetrics::getTotalInteractions)
                    .sum();

            // 3) promedio de engagement rate (interactions/views)
            // asumimos que getEngagement ya te da (interactions/views) o bien lo recalculas:
            double promedioEngagement = listaUsuario.stream()
                    .mapToDouble(UserTiktokMetrics::getEngagement)
                    .average()
                    .orElse(0.0);

            // 4) añades al dashboard
            dashboard.add(new MetricData(
                    "metricsByUsername",
                    username,
                    sumaVistas,
                    sumaLikes,
                    (long)promedioEngagement, sumInteractions
            ));
        }}

        Map<DayOfWeek, List<UserTiktokMetrics>> porDia = metricasFiltradas.stream()
                .filter(m -> m.getDatePosted() != null)
                .collect(Collectors.groupingBy(
                        m -> m.getDatePosted().getDayOfWeek()
                ));

        porDia.forEach((dayOfWeek, lista) -> {
            String nombreDia = dayOfWeek
                    .getDisplayName(TextStyle.FULL, new Locale("es", "PE"));
            // 1) Suma de vistas
            long sumaVistas = lista.stream()
                    .mapToLong(UserTiktokMetrics::getViews)
                    .sum();

            // 2) Suma de likes
            long sumaLikes = lista.stream()
                    .mapToLong(UserTiktokMetrics::getLikes)
                    .sum();

            // 3) Promedio de engagement rate
            double promedioEngagement = lista.stream()
                    .mapToDouble(UserTiktokMetrics::getEngagement)
                    .average()
                    .orElse(0.0);

            // 4) Añadir a dashboard
            dashboard.add(new MetricData(
                    "byDayOfWeek",
                    nombreDia,          // p.ej. "lunes"
                    sumaVistas,
                    sumaLikes,
                    (long)promedioEngagement,0
            ));
        });

        encasulador.add(dashboard);
        return encasulador;
    }

    private UserTiktokMetricsResponseDTO toDto(UserTiktokMetrics e) {
        UserTiktokMetricsResponseDTO dto = new UserTiktokMetricsResponseDTO();
        dto.setId(e.getId());
        dto.setUserId(e.getUser().getId());                // sólo el ID, no la entidad entera
        dto.setPostId(e.getPostId());
        dto.setDatePosted(e.getDatePosted());
        dto.setHourPosted(e.getHourPosted());
        dto.setUsernameTiktokAccount(e.getUsernameTiktokAccount());
        dto.setPostURL(e.getPostURL());
        dto.setViews(e.getViews());
        dto.setLikes(e.getLikes());
        dto.setComments(e.getComments());
        dto.setSaves(e.getSaves());
        dto.setReposts(e.getReposts());
        dto.setTotalInteractions(e.getTotalInteractions());
        dto.setEngagement(e.getEngagement());
        dto.setNumberHashtags(e.getNumberHashtags());
        dto.setHashtags(e.getHashtags());
        dto.setSoundId(e.getSoundId());
        dto.setSoundURL(e.getSoundURL());
        dto.setRegionPost(e.getRegionPost());
        dto.setDateTracking(e.getDateTracking());
        dto.setTimeTracking(e.getTimeTracking());
        return dto;
    }
}
