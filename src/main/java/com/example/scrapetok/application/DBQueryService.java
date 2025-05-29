package com.example.scrapetok.application;

import com.example.scrapetok.domain.DTO.UserDBQueryRequest;
import com.example.scrapetok.domain.DTO.UserTiktokMetricsResponseDTO;
import com.example.scrapetok.domain.UserTiktokMetrics;
import com.example.scrapetok.exception.ResourceNotFoundException;
import com.example.scrapetok.repository.UserTiktokMetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DBQueryService {
    @Autowired
    private UserTiktokMetricsRepository userTiktokMetricsRepository;

    //Busca registros de métricas aplicando filtros del request.
    public List<UserTiktokMetricsResponseDTO> buscarConFiltros(UserDBQueryRequest req) {
        var spec = UserTiktokMetricsSpecification.filterBy(req);
        var sort = Sort.by(
                Sort.Order.desc("datePosted"),
                Sort.Order.desc("hourPosted")
        );
        return userTiktokMetricsRepository.findAll(spec, sort).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
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
