package com.example.scrapetok.repository;

import com.example.scrapetok.domain.UserTiktokMetrics;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface UserTiktokMetricsRepository extends JpaRepository<UserTiktokMetrics,Long>, JpaSpecificationExecutor<UserTiktokMetrics> {
    List<UserTiktokMetrics> findUsernameTiktokAccountByUserId(Long userId);

    List<UserTiktokMetrics> findAll(Specification<UserTiktokMetrics> spec, Sort sort);



    public interface HourlyInteractions {
        Integer getHour();             // la hora del día (0–23)

        Long getTotalInteractions(); // suma de totalInteractions
    }
    @Query("""
            SELECT 
        EXTRACT(HOUR FROM ut.hourPosted)                      AS hour,
        SUM(ut.totalInteractions)                             AS totalInteractions
      FROM UserTiktokMetrics ut
      WHERE ut.dateTracking >= :since                           
      GROUP BY EXTRACT(HOUR FROM ut.hourPosted)
      ORDER BY EXTRACT(HOUR FROM ut.hourPosted)
      """)
    List<HourlyInteractions> findTotalInteractionsByHourPostedSince(
            @Param("since") LocalDate since
    );



    public interface HashtagEngagement {
        String getHashtag();
        Double getAvgEngagement();
    }
    @Query(value = """
      SELECT 
        tag                                   AS hashtag,
        AVG(engagement)                       AS avg_engagement
      FROM (
        SELECT 
          TRIM(tag)       AS tag,
          engagement
        FROM user_tiktok_metrics,
             unnest(string_to_array(hashtags, ',')) AS tag
        WHERE user_tiktok_metrics.date_tracking >= :since
      ) sub
      GROUP BY tag
      ORDER BY avg_engagement DESC
      """, nativeQuery = true)
    List<HashtagEngagement> findAvgEngagementByHashtagSince(@Param("since") LocalDate since);


    public interface SoundEngagement {
        String getSoundId();
        Double getAvgEngagement();
    }
    @Query("""
      SELECT 
        ut.soundId                  AS soundId,
        AVG(ut.engagement)          AS avgEngagement
      FROM UserTiktokMetrics ut
      WHERE ut.dateTracking >= :since
      GROUP BY ut.soundId
      ORDER BY AVG(ut.engagement) DESC
      """)
    List<SoundEngagement> findAvgEngagementBySoundSince(@Param("since") LocalDate since);
}
