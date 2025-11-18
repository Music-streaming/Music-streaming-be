package com.example.Music_streaming.youtube;

import lombok.Data;

import java.util.List;

@Data
public class YouTubeSearchResponse {
    private List<Item> items;

    @Data
    public static class Item {
        private Id id;

        @Data
        public static class Id {
            private String kind;
            private String videoId;
        }
    }
}
