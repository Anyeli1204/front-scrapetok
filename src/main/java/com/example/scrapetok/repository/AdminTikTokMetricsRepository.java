package com.example.scrapetok.repository;

import com.example.scrapetok.domain.AdminTiktokMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminTikTokMetricsRepository extends JpaRepository<AdminTiktokMetrics,Long> {

    // Cuenta la cantidad total por cada región de los posts más recientes -> publicados hoy
    interface RegionVideoCount {
        String getRegionPost();
        Long   getNroVideos();
    }
    @Query(value = """
        SELECT s.region_post   AS regionPost,
               COUNT(s.id)     AS nroVideos
        FROM   admin_tiktok_metrics s
        INNER JOIN (
            SELECT posturl,
                   MAX(id) AS reciente
            FROM   admin_tiktok_metrics
            WHERE  date_tracking = CURRENT_DATE
            GROUP  BY posturl
        ) f 
          ON s.id = f.reciente
        GROUP  BY s.region_post
        """,
            nativeQuery = true
    )
    List<RegionVideoCount> countTodayRecentVideosByRegion();


    //Cuenta las vistas totales por cada hashtag de los posts más recientes de hoy.
    interface HashtagViewCount {
        String getHashtag();
        Long   getTotalVistas();
    }
    @Query(value = """
        -- language=SQL
        SELECT
          TRIM(hashtag)      AS hashtag,
          SUM(views)         AS total_vistas
        FROM (
          SELECT
            UNNEST(STRING_TO_ARRAY(s.hashtags, ', ')) AS hashtag,
            s.views
          FROM admin_tiktok_metrics s
          INNER JOIN (
            SELECT u.posturl,
                   MAX(u.id) AS reciente
            FROM   admin_tiktok_metrics u
            WHERE  u.date_tracking = CURRENT_DATE
            GROUP  BY u.posturl
          ) f ON s.id = f.reciente
        ) separados
        GROUP BY hashtag
        """,
            nativeQuery = true
    )
    List<HashtagViewCount> countTodayViewsByHashtag();


    //Suma las vistas por cada sound_id de los posts más recientes de hoy.
    interface SoundViewCount {
        Long getSoundId();
        Long getTotalViews();
    }
    @Query(value = """
        -- language=SQL
        SELECT
          ult.sound_id    AS soundId,
          SUM(ult.views)  AS totalViews
        FROM (
          SELECT *
          FROM admin_tiktok_metrics u
          WHERE u.id = (
            SELECT MAX(id)
            FROM admin_tiktok_metrics
            WHERE posturl = u.posturl
              AND DATE(date_tracking) = CURRENT_DATE
          )
        ) ult
        GROUP BY ult.sound_id
        """,
            nativeQuery = true
    )
    List<SoundViewCount> countTodayViewsBySound();


    // Suma vistas y likes por región de los posts más recientes de hoy.
    interface RegionMetricsCount {
        String getRegionPost();
        Long   getTotalViews();
        Long   getTotalLikes();
    }
    @Query(value = """
        -- language=SQL
        SELECT
          ult.region_post    AS regionPost,
          SUM(ult.views)     AS totalViews,
          SUM(ult.likes)     AS totalLikes
        FROM (
          SELECT *
          FROM admin_tiktok_metrics u
          WHERE u.id = (
            SELECT MAX(id)
            FROM admin_tiktok_metrics
            WHERE posturl = u.posturl
              AND DATE(date_tracking) = CURRENT_DATE
          )
        ) ult
        GROUP BY ult.region_post
        """,
            nativeQuery = true
    )
    List<RegionMetricsCount> countTodayViewsAndLikesByRegion();












/*
    @Query(value = """
    SELECT username_tiktok_account AS username, SUM(views) AS total_views
    FROM user_tiktok_metrics
    WHERE date_tracking = CURRENT_DATE
    GROUP BY username_tiktok_account
    """, nativeQuery = true)
    List<Map<String, Object>> findViewsGroupedByUsernameForToday();


    @Query(value = """
    SELECT sound_id AS username, SUM(views) AS total_views
    FROM user_tiktok_metrics
    WHERE date_tracking = CURRENT_DATE
    GROUP BY sound_id
    """, nativeQuery = true)
    List<Map<String, Object>> findViewsGroupedByMusicIdForToday();


    @Query(value = """
    SELECT date_posted, SUM(views) AS total_views
    FROM user_tiktok_metrics
    WHERE date_tracking = CURRENT_DATE
    GROUP BY date_posted
    ORDER BY date_posted
    """, nativeQuery = true)
    List<Map<String, Object>> findViewsGroupedByPostDateTrackedToday();

    @Query(value = """
    SELECT 
        TRIM(unnest(string_to_array(hashtags, ','))) AS hashtag,
        COUNT(*) AS frecuencia,
        ROUND(AVG(views), 2) AS promedio_views
    FROM user_tiktok_metrics
    WHERE date_tracking = CURRENT_DATE AND hashtags IS NOT NULL
    GROUP BY hashtag
    ORDER BY frecuencia DESC
    LIMIT 15
    """, nativeQuery = true)
    List<HashtagTrendDTO> findTopHashtagsTrendingToday();

 */
}
