package com.example.scrapetok.application;
import com.example.scrapetok.domain.DTO.UserDBQueryRequest;
import com.example.scrapetok.domain.UserTiktokMetrics;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class UserTiktokMetricsSpecification {
    public static Specification<UserTiktokMetrics> filterBy(UserDBQueryRequest req) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Subquery para obtener el último registro por postId
            var sub = query.subquery(Long.class);
            Root<UserTiktokMetrics> subRoot = sub.from(UserTiktokMetrics.class);
            sub.select(cb.max(subRoot.get("id")));
            List<Predicate> subPreds = new ArrayList<>();
            // Filtrar por userId
            if (req.getUserId() != null) {
                subPreds.add(
                        cb.equal(subRoot.get("user").get("id"), req.getUserId())
                );
            }
            // Correlación: mismo postId que la fila externa
            subPreds.add(
                    cb.equal(subRoot.get("postId"), root.get("postId"))
            );
            sub.where(subPreds.toArray(new Predicate[0]));
            sub.groupBy(subRoot.get("postId"));

            // Restricción principal: solo registros con id = max(id) del subquery
            predicates.add(cb.equal(root.get("id"), sub));

            if (req.getPostId() != null) {
                List<String> postIds = Arrays.stream(req.getPostId().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
                if (!postIds.isEmpty()) {
                    predicates.add(root.get("postId").in(postIds));
                }
            }
            // postURL
            if (req.getPostURL() != null) {
                List<String> urls = Arrays.stream(req.getPostURL().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
                if (!urls.isEmpty()) {
                    predicates.add(root.get("postURL").in(urls));
                }
            }
            // tiktokUsernames
            if (req.getTiktokUsernames() != null) {
                List<String> users = Arrays.stream(req.getTiktokUsernames().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
                if (!users.isEmpty()) {
                    predicates.add(root.get("usernameTiktokAccount").in(users));
                }
            }
            // regionPost
            if (req.getRegionPost() != null) {
                List<String> regions = Arrays.stream(req.getRegionPost().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
                if (!regions.isEmpty()) {
                    predicates.add(root.get("regionPost").in(regions));
                }
            }
            // soundId
            if (req.getSoundId() != null) {
                List<String> sounds = Arrays.stream(req.getSoundId().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
                if (!sounds.isEmpty()) {
                    predicates.add(root.get("soundId").in(sounds));
                }
            }
            // soundURL
            if (req.getSoundURL() != null) {
                List<String> soundUrls = Arrays.stream(req.getSoundURL().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
                if (!soundUrls.isEmpty()) {
                    predicates.add(root.get("soundURL").in(soundUrls));
                }
            }

            // Filtros de rangos de fecha
            if (req.getDatePostedFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("datePosted"), req.getDatePostedFrom()));
            }
            if (req.getDatePostedTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("datePosted"), req.getDatePostedTo()));
            }

            // Hashtag
            if (req.getHashtags() != null && !req.getHashtags().isEmpty()) {
                List<String> tags = Arrays.stream(req.getHashtags().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
                if (!tags.isEmpty()) {
                    List<Predicate> tagPreds = new ArrayList<>();
                    for (String tag : tags) {
                        tagPreds.add(cb.like(root.get("hashtags"), "%" + tag + "%"));
                    }
                    predicates.add(cb.or(tagPreds.toArray(new Predicate[0])));
                }
            }

            // Filtros numéricos con <= y >= según corresponda
            if (req.getMinViews() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("views"), req.getMinViews()));
            }
            if (req.getMaxViews() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("views"), req.getMaxViews()));
            }
            if (req.getMinLikes() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("likes"), req.getMinLikes()));
            }
            if (req.getMaxLikes() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("likes"), req.getMaxLikes()));
            }
            if (req.getMinTotalInteractions() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("totalInteractions"), req.getMinTotalInteractions()));
            }
            if (req.getMaxTotalInteractions() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("totalInteractions"), req.getMaxTotalInteractions()));
            }
            if (req.getMinEngagement() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("engagement"), req.getMinEngagement()));
            }
            if (req.getMaxEngagement() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("engagement"), req.getMaxEngagement()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
