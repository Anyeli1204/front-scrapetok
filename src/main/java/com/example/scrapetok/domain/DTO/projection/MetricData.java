package com.example.scrapetok.domain.DTO.projection;

public class MetricData {
    private final String type;
    private final String category;
    private final long views;
    private final long likes;
    private final long avgEngagement;
    private final long interactions;


    public MetricData(String type, String category, long views, long likes, long avgEngagement, long interactions) {
        this.type     = type;
        this.category = category;
        this.views    = views;
        this.likes    = likes;
        this.avgEngagement = avgEngagement;
        this.interactions = interactions;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public long getViews() {
        return views;
    }

    public long getLikes() {
        return likes;
    }
    public long getAvgEngagement() {
        return avgEngagement;
    }
    public long getInteractions() {
        return interactions;
    }

    @Override
    public String toString() {
        return "MetricData{" +
                "type='"     + type     + '\'' +
                ", category='" + category + '\'' +
                ", views="     + views    +
                ", likes="     + likes    +
                '}';
    }
}
